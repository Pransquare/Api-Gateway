pipeline {
    agent any

    environment {
        SERVICE_NAME = "api-gateway"
        EC2_USER = "ec2-user"
        EC2_HOST = "13.51.195.68"
        REMOTE_DIR = "/home/ec2-user/api-gateway"
        JAR_NAME = "api-gateway.jar"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Pransquare/API-Gateway.git'
            }
        }

        stage('Build JAR') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Create Remote Directory') {
            steps {
                withCredentials([file(credentialsId: 'ec2-pem', variable: 'EC2_KEY')]) {
                    bat """
                    ssh -i "%EC2_KEY%" -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "mkdir -p %REMOTE_DIR%"
                    """
                }
            }
        }

        stage('Copy JAR to EC2') {
            steps {
                withCredentials([file(credentialsId: 'ec2-pem', variable: 'EC2_KEY')]) {
                    bat """
                    scp -i "%EC2_KEY%" -o StrictHostKeyChecking=no target\\%JAR_NAME% %EC2_USER%@%EC2_HOST%:%REMOTE_DIR%/
                    """
                }
            }
        }

        stage('Deploy App') {
            steps {
                withCredentials([file(credentialsId: 'ec2-pem', variable: 'EC2_KEY')]) {
                    bat """
                    ssh -i "%EC2_KEY%" -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "
                        pkill -f %JAR_NAME% || true
                        nohup java -jar %REMOTE_DIR%/%JAR_NAME% > %REMOTE_DIR%/app.log 2>&1 &
                    "
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful!"
        }
        failure {
            echo "❌ Deployment failed. Check console logs for details."
        }
    }
}
