#!/bin/bash
# ========================================
# Build and Push All Services to Docker Hub
# Run this script locally before deploying to EC2
# ========================================

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}🐳 Building and Pushing All Services${NC}"
echo -e "${GREEN}========================================${NC}"

# Configuration
DOCKER_USERNAME="hanhdau"
IMAGE_TAG="${IMAGE_TAG:-latest}"

# Define all services
declare -a SERVICES=(
    "job-manager-auth:jm-auth"
    "job-manager-company:jm-company"
    "job-manager-jobpost:jm-jobpost"
    "job-manager-applicant-search:jm-applicant-search"
    "job-manager-subscription:jm-subscription"
    "job-manager-payment:jm-payment"
    "job-manager-notification:jm-notification"
    "job-manager-gateway:jm-gateway"
    "job-manager-discovery:jm-discovery"
)

# Frontend
FRONTEND_SERVICE="../JobManager_FE:jm-frontend"

# Step 1: Login to Docker Hub
echo -e "${YELLOW}🔐 Logging into Docker Hub...${NC}"
if [ -f ".env" ]; then
    export $(cat .env | grep "DOCKERHUB_TOKEN" | xargs)
    echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKER_USERNAME" --password-stdin
    echo -e "${GREEN}✅ Docker login successful${NC}"
else
    echo -e "${RED}❌ .env file not found!${NC}"
    exit 1
fi

# Step 2: Build and push backend services
echo -e "${YELLOW}📦 Building Backend Services...${NC}"
echo ""

for service in "${SERVICES[@]}"; do
    IFS=':' read -r dir image <<< "$service"
    
    if [ -d "$dir" ]; then
        echo -e "${BLUE}Building $image from $dir...${NC}"
        
        # Build the image
        if docker build -t "$DOCKER_USERNAME/$image:$IMAGE_TAG" "./$dir"; then
            echo -e "${GREEN}✅ Built $image${NC}"
            
            # Push to Docker Hub
            echo -e "${BLUE}Pushing $image to Docker Hub...${NC}"
            if docker push "$DOCKER_USERNAME/$image:$IMAGE_TAG"; then
                echo -e "${GREEN}✅ Pushed $image${NC}"
            else
                echo -e "${RED}❌ Failed to push $image${NC}"
                exit 1
            fi
        else
            echo -e "${RED}❌ Failed to build $image${NC}"
            exit 1
        fi
        
        echo ""
    else
        echo -e "${YELLOW}⚠️  Directory $dir not found, skipping...${NC}"
    fi
done

# Step 3: Build and push frontend
echo -e "${YELLOW}📦 Building Frontend Service...${NC}"
IFS=':' read -r dir image <<< "$FRONTEND_SERVICE"

if [ -d "$dir" ]; then
    echo -e "${BLUE}Building $image from $dir...${NC}"
    
    if docker build -t "$DOCKER_USERNAME/$image:$IMAGE_TAG" "$dir"; then
        echo -e "${GREEN}✅ Built $image${NC}"
        
        echo -e "${BLUE}Pushing $image to Docker Hub...${NC}"
        if docker push "$DOCKER_USERNAME/$image:$IMAGE_TAG"; then
            echo -e "${GREEN}✅ Pushed $image${NC}"
        else
            echo -e "${RED}❌ Failed to push $image${NC}"
            exit 1
        fi
    else
        echo -e "${RED}❌ Failed to build $image${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠️  Frontend directory not found at $dir, skipping...${NC}"
fi

# Step 4: Clean up local images (optional)
echo ""
echo -e "${YELLOW}🧹 Cleanup Options:${NC}"
read -p "Do you want to remove local images to save space? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Removing local images...${NC}"
    for service in "${SERVICES[@]}"; do
        IFS=':' read -r dir image <<< "$service"
        docker rmi "$DOCKER_USERNAME/$image:$IMAGE_TAG" 2>/dev/null || true
    done
    docker rmi "$DOCKER_USERNAME/jm-frontend:$IMAGE_TAG" 2>/dev/null || true
    echo -e "${GREEN}✅ Local images removed${NC}"
fi

# Step 5: Summary
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✅ Build and Push Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${BLUE}📝 Pushed Images:${NC}"
for service in "${SERVICES[@]}"; do
    IFS=':' read -r dir image <<< "$service"
    echo -e "   $DOCKER_USERNAME/$image:$IMAGE_TAG"
done
echo -e "   $DOCKER_USERNAME/jm-frontend:$IMAGE_TAG"
echo ""
echo -e "${YELLOW}⚠️  Next Steps:${NC}"
echo -e "   1. SSH into EC2-2 and run: ./scripts/deploy-ec2-2.sh"
echo -e "   2. SSH into EC2-1 and run: ./scripts/deploy-ec2-1.sh"
echo -e "   3. Verify services are healthy"
echo ""
echo -e "${BLUE}📋 Manual Deploy Commands:${NC}"
echo -e "   ssh -i ~/.ssh/mulan-job-manager-app-ec2-key.pem ubuntu@EC2-2-IP"
echo -e "   cd /home/ubuntu/job-manager && ./scripts/deploy-ec2-2.sh"
echo ""
echo -e "   ssh -i ~/.ssh/mulan-job-manager-app-ec2-key.pem ubuntu@EC2-1-IP"
echo -e "   cd /home/ubuntu/job-manager && ./scripts/deploy-ec2-1.sh"
echo ""
