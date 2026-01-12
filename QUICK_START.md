# 🚀 Quick Start - Deploy to EC2

## ⚡ Fast Track Deployment (5 Steps)

### Step 1: Connect to EC2 and Install Docker (5 minutes)

```bash
# Connect
ssh -i mulan-job-manager-app-ec2-key.pem ec2-user@13.250.200.49

# Run setup script (copy-paste this entire block)
sudo yum update -y && \
sudo yum install docker -y && \
sudo systemctl start docker && \
sudo systemctl enable docker && \
sudo usermod -a -G docker ec2-user && \
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
sudo chmod +x /usr/local/bin/docker-compose && \
mkdir -p /home/ec2-user/job-manager

# Log out and back in
exit
```

---

### Step 2: Transfer Files from Local Machine (1 minute)

**Open PowerShell in project directory:**

```powershell
# Run the transfer script
.\scripts\transfer-to-ec2.ps1
```

**OR manually:**

```powershell
# Set SSH key permissions
icacls mulan-job-manager-app-ec2-key.pem /inheritance:r
icacls mulan-job-manager-app-ec2-key.pem /grant:r "$($env:USERNAME):(R)"

# Transfer files
scp -i mulan-job-manager-app-ec2-key.pem docker-compose.prod.yml ec2-user@13.250.200.49:/home/ec2-user/job-manager/
scp -i mulan-job-manager-app-ec2-key.pem .env ec2-user@13.250.200.49:/home/ec2-user/job-manager/
scp -i mulan-job-manager-app-ec2-key.pem scripts/deploy.sh ec2-user@13.250.200.49:/home/ec2-user/job-manager/
```

---

### Step 3: Start Services on EC2 (2 minutes)

```bash
# Reconnect to EC2
ssh -i mulan-job-manager-app-ec2-key.pem ec2-user@13.250.200.49

# Navigate to directory
cd /home/ec2-user/job-manager

# Add Docker Hub username
echo "DOCKERHUB_USERNAME=tyuong" >> .env

# Make script executable
chmod +x deploy.sh

# Pull images and start
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d

# Check status
docker compose -f docker-compose.prod.yml ps
```

---

### Step 4: Configure AWS Security Group (2 minutes)

**In AWS Console:**
1. Go to **EC2** → **Security Groups**
2. Select security group for instance `i-04d330d2f448ca840`
3. **Edit Inbound Rules** → **Add Rule**:
   - Type: **Custom TCP**
   - Port: **8080**
   - Source: **0.0.0.0/0** (or your IP)
   - Description: **API Gateway**

---

### Step 5: Configure GitHub Secrets (3 minutes)

**Go to GitHub**: Your Repo → Settings → Secrets and variables → Actions

**Add 5 secrets:**

| Secret Name | Value |
|------------|-------|
| `DOCKERHUB_USERNAME` | `tyuong` |
| `DOCKERHUB_TOKEN` | Get from https://hub.docker.com/settings/security |
| `EC2_HOST` | `13.250.200.49` |
| `EC2_USER` | `ec2-user` |
| `EC2_SSH_KEY` | Copy entire contents of `mulan-job-manager-app-ec2-key.pem` |

---

## ✅ Verify Deployment

```bash
# Test API (from local machine or browser)
curl http://13.250.200.49:8080/api/auth/health

# Check Eureka Dashboard
http://13.250.200.49:8761
```

---

## 🎯 Future Deployments (Automatic)

After initial setup, deployments are **100% automatic**:

1. Make changes to your code
2. Commit and push to `main` branch
3. GitHub Actions automatically:
   - Builds Docker images
   - Pushes to Docker Hub
   - Deploys to EC2
   - Restarts services

**Monitor**: GitHub → Actions tab

---

## 📋 Deployment Checklist

- [ ] EC2 accessible via SSH
- [ ] Docker installed on EC2
- [ ] Files transferred to `/home/ec2-user/job-manager`
- [ ] Services running: `docker compose ps` shows all UP
- [ ] Security Group allows port 8080
- [ ] API responds: `curl http://13.250.200.49:8080/api/auth/health`
- [ ] All 5 GitHub Secrets configured
- [ ] Eureka shows all services registered: http://13.250.200.49:8761
- [ ] Test deployment: Push to main branch

---

## 🆘 Common Issues

### "Permission denied" when connecting
```bash
# Fix SSH key permissions (Windows PowerShell)
icacls mulan-job-manager-app-ec2-key.pem /inheritance:r
icacls mulan-job-manager-app-ec2-key.pem /grant:r "$($env:USERNAME):(R)"
```

### "Connection refused" on port 8080
- Check AWS Security Group allows port 8080
- Verify services are running: `docker compose ps`

### Services not starting
```bash
# Check logs
docker compose -f docker-compose.prod.yml logs

# Restart specific service
docker compose -f docker-compose.prod.yml restart gateway
```

### GitHub Actions deployment fails
1. Verify all 5 secrets are set correctly
2. Check Actions logs for specific error
3. Ensure EC2 security group allows SSH from anywhere

---

## 📚 Full Documentation

For detailed information, see:
- **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Complete deployment documentation
- **[README.md](README.md)** - Project overview and API documentation

---

## 🎉 Success!

Your Job Manager Backend is now deployed on EC2!

**Your API URL**: `http://13.250.200.49:8080`

**Services:**
- Gateway: http://13.250.200.49:8080
- Eureka: http://13.250.200.49:8761
- Auth: http://13.250.200.49:8081
- Company: http://13.250.200.49:8082
- Job Post: http://13.250.200.49:8083
- Applicant Search: http://13.250.200.49:8084
- Subscription: http://13.250.200.49:8085
- Payment: http://13.250.200.49:8086
- Notification: http://13.250.200.49:8087

All requests should go through the **Gateway** at port **8080**.
