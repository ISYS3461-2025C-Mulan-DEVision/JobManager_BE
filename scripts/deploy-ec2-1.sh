#!/bin/bash
# ========================================
# EC2-1 Deployment Script
# Edge & Control Plane: Gateway, Discovery, Frontend
# ========================================

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}🚀 Deploying to EC2-EDGE (FE, Gateway, Discovery)${NC}"
echo -e "${GREEN}========================================${NC}"

# Configuration
# Automatically detect the project directory (parent of scripts directory)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DEPLOY_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"
IMAGE_TAG="${IMAGE_TAG:-latest}"
COMPOSE_FILE="docker-compose.ec2-1.yml"

echo -e "${BLUE}📁 Deploy directory: $DEPLOY_DIR${NC}"

# Step 1: Load environment variables
echo -e "${YELLOW}📋 Loading environment variables...${NC}"
if [ -f "$DEPLOY_DIR/.env" ]; then
    export $(cat "$DEPLOY_DIR/.env" | grep -v '^#' | xargs)
    echo -e "${GREEN}✅ Environment variables loaded${NC}"
else
    echo -e "${RED}❌ .env file not found!${NC}"
    exit 1
fi

# Step 2: Pull latest code
echo -e "${YELLOW}📥 Pulling latest deployment configuration...${NC}"
cd "$DEPLOY_DIR"
git pull origin feat/deploy-ultimo || echo -e "${YELLOW}⚠️  Git pull skipped (may not be in a git repo or branch not tracking)${NC}"
echo -e "${GREEN}✅ Code updated${NC}"

# Step 3: Pull Docker images
echo -e "${YELLOW}🐳 Pulling Docker images...${NC}"
docker compose -f "$COMPOSE_FILE" pull
echo -e "${GREEN}✅ Images pulled${NC}"

# Step 4: Stop old containers
echo -e "${YELLOW}🛑 Stopping old containers...${NC}"
docker compose -f "$COMPOSE_FILE" down
echo -e "${GREEN}✅ Old containers stopped${NC}"

# Step 5: Start new containers
echo -e "${YELLOW}▶️  Starting new containers...${NC}"
docker compose -f "$COMPOSE_FILE" up -d
echo -e "${GREEN}✅ Containers started${NC}"

# Step 6: Wait for services to be ready
echo -e "${YELLOW}⏳ Waiting for services to be healthy...${NC}"
sleep 30

# Step 7: Health checks
echo -e "${YELLOW}🏥 Running health checks...${NC}"

# Check Discovery
if curl -sf http://localhost:8761/actuator/health > /dev/null; then
    echo -e "${GREEN}✅ Discovery is healthy${NC}"
else
    echo -e "${RED}❌ Discovery health check failed${NC}"
    docker compose -f "$COMPOSE_FILE" logs discovery-server
    exit 1
fi

# Check Gateway
if curl -sf http://localhost:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}✅ Gateway is healthy${NC}"
else
    echo -e "${RED}❌ Gateway health check failed${NC}"
    docker compose -f "$COMPOSE_FILE" logs gateway
    exit 1
fi

# Check Frontend
if curl -sf http://localhost/ > /dev/null; then
    echo -e "${GREEN}✅ Frontend is accessible${NC}"
else
    echo -e "${RED}❌ Frontend health check failed${NC}"
    docker compose -f "$COMPOSE_FILE" logs frontend
    exit 1
fi

# Step 8: Clean up
echo -e "${YELLOW}🧹 Cleaning up old images...${NC}"
docker image prune -f
echo -e "${GREEN}✅ Cleanup complete${NC}"

# Step 9: Display running containers
echo -e "${YELLOW}📊 Running containers:${NC}"
docker compose -f "$COMPOSE_FILE" ps

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✅ EC2-1 Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}📦 Image Tag: ${IMAGE_TAG}${NC}"
echo -e "${GREEN}⏰ Time: $(date)${NC}"
echo -e "${GREEN}========================================${NC}"
