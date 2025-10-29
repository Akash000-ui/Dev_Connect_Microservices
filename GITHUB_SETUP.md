# GitHub Repository Setup Script for Dev-Connect

## Step 1: Create GitHub Repository

1. Go to [GitHub](https://github.com/new)
2. Repository name: `Dev-Connect`
3. Description: `Professional Social Network Platform - Microservices with Spring Boot, Angular 18, MongoDB Atlas & Kafka`
4. Set as **Public** or **Private** (your choice)
5. **DO NOT** initialize with README (we already have one)
6. Click "Create repository"

## Step 2: Push to GitHub

After creating the repository, run these commands in PowerShell:

```powershell
# Add remote origin (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/Dev-Connect.git

# Verify remote
git remote -v

# Push to main branch
git branch -M main
git push -u origin main
```

## Step 3: Verify Upload

Go to: `https://github.com/YOUR_USERNAME/Dev-Connect`

You should see all your files uploaded!

## Step 4: Setup Branch Protection (Optional but Recommended)

1. Go to repository Settings > Branches
2. Click "Add rule"
3. Branch name pattern: `main`
4. Enable:
   - ✅ Require pull request reviews before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - ✅ Include administrators

## Step 5: Add Collaborators (Optional)

1. Go to Settings > Collaborators
2. Click "Add people"
3. Enter GitHub usernames of your team members

## Repository Structure

```
Dev-Connect/
├── Auth-Service/          # Authentication microservice
├── ProfileService/        # Profile management microservice  
├── Api-Gateway/           # API Gateway with routing
├── Eureka-Register/       # Service discovery
├── dev-connect-frontend/  # Angular 18 frontend
├── .gitignore            # Git ignore rules
└── README.md             # Project documentation
```

## Next Steps After Pushing

### 1. Setup GitHub Actions CI/CD (Optional)
Create `.github/workflows/ci-cd.yml` for automated testing and deployment

### 2. Setup GitHub Secrets
Go to Settings > Secrets and variables > Actions

Add these secrets:
- `MONGODB_AUTH_URI` - MongoDB connection string for auth database
- `MONGODB_PROFILE_URI` - MongoDB connection string for profile database
- `JWT_SECRET` - Your JWT secret key
- `GOOGLE_CLIENT_ID` - Google OAuth client ID

### 3. Enable GitHub Pages (Optional)
For hosting documentation or frontend demo

### 4. Setup Issue Templates
Create `.github/ISSUE_TEMPLATE/` for standardized bug reports and feature requests

### 5. Add Status Badges to README
Update README.md with build status, code coverage, etc.

## Useful Git Commands

```powershell
# Check status
git status

# Pull latest changes
git pull origin main

# Create new branch
git checkout -b feature/your-feature-name

# Commit changes
git add .
git commit -m "Your commit message"

# Push changes
git push origin your-branch-name

# Switch branches
git checkout main

# View commit history
git log --oneline --graph --all

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Discard local changes
git checkout -- .
```

## Team Workflow

1. **Main branch** - Production-ready code
2. **Develop branch** - Integration branch for features
3. **Feature branches** - Individual feature development
4. **Pull Requests** - Code review before merging

## Questions?

Visit: https://docs.github.com/en/get-started
