pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3'
        git 'Git'
    }

    environment {
        EC2_USER = "ec2-user"
        EC2_HOST = "13.51.195.68"
        EC2_DIR  = "/home/ec2-user/api-gateway"
        SSH_KEY  = credentials('ec2-ssh-key') // your Jenkins SSH key ID
        JAR_NAME = "api-gateway.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Pransquare/Api-Gateway.git'
            }
        }

        stage('Build JAR') {
            steps {
                bat "mvn clean package -DskipTests"
            }
        }

        stage('Test SSH Connection') {
            steps {
                bat '"C:\\Program Files\\Git\\usr\\bin\\ssh.exe" -i %SSH_KEY% -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "hostname; whoami"'
            }
        }

        stage('Prepare EC2 Directory') {
            steps {
                bat '"C:\\Program Files\\Git\\usr\\bin\\ssh.exe" -i %SSH_KEY% -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "mkdir -p %EC2_DIR%"'
            }
        }

        stage('Copy JAR to EC2') {
            steps {
                echo "Copying JAR to EC2..."
                bat '"C:\\Program Files\\Git\\usr\\bin\\scp.exe" -i %SSH_KEY% target\\%JAR_NAME% %EC2_USER%@%EC2_HOST%:%EC2_DIR%/'
            }
        }

        stage('Deploy App') {
            steps {
                echo "Stopping previous app (if running) and starting new deployment..."
                bat '"C:\\Program Files\\Git\\usr\\bin\\ssh.exe" -i %SSH_KEY% -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "pid=\\$(pgrep -f %JAR_NAME%); if [ -n \\"\\$pid\\" ]; then echo \'App running, stopping it...\'; kill -9 \\"\\$pid\\"; else echo \'No previous app running\'; fi; echo \'Starting new app...\'; nohup java -jar %EC2_DIR%/%JAR_NAME% > /dev/null 2>&1 &"'
            }
        }

        stage('Verify App') {
            steps {
                bat '"C:\\Program Files\\Git\\usr\\bin\\ssh.exe" -i %SSH_KEY% -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "ps -ef | grep java | grep %JAR_NAME%"'
            }
        }
    }

    post {
        failure {
            echo "Deployment failed ❌"
        }
        success {
            echo "Deployment succeeded ✅"
        }
    }
}
