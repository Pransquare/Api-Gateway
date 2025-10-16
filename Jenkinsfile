pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.53.39.170"
        EC2_USER = "ec2-user"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Pransquare/Api-Gateway.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to EC2') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ec2-key', keyFileVariable: 'EC2_KEY')]) {
                    bat """
                    echo Fixing key permissions...
                    icacls "%EC2_KEY%" /inheritance:r
                    icacls "%EC2_KEY%" /grant:r "%USERNAME%:R"
                    icacls "%EC2_KEY%" /remove "Users" "BUILTIN\\Users" "Everyone"

                    echo Creating directory on EC2...
                    ssh -i "%EC2_KEY%" -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "mkdir -p %DEPLOY_DIR%"

                    echo Copying JAR to EC2...
                    scp -i "%EC2_KEY%" -o StrictHostKeyChecking=no target\\api-gateway-0.0.1-SNAPSHOT.jar %EC2_USER%@%EC2_HOST%:%DEPLOY_DIR%/

                    echo Restarting application on EC2...
                    ssh -i "%EC2_KEY%" -o StrictHostKeyChecking=no %EC2_USER%@%EC2_HOST% "nohup java -jar %DEPLOY_DIR%/api-gateway-0.0.1-SNAPSHOT.jar > %DEPLOY_DIR%/app.log 2>&1 &"
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Deployment successful!'
        }
        failure {
            echo '❌ Deployment failed!'
        }
    }
}
