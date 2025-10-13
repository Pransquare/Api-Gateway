pipeline {
    agent any

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        DEPLOY_DIR = "C:\\Apps\\api-gateway"
        SERVICE_NAME = "api-gateway"
        SERVICE_PORT = "8765"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out API Gateway repo..."
                git branch: 'master', url: 'https://github.com/Pransquare/Api-Gateway.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building API Gateway..."
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    // ✅ Define variables inside this script block
                    def EC2_USER = "Administrator"
                    def EC2_HOST = "51.20.72.212"   // Replace with your EC2 public IP
                    def PEM_PATH = def PEM_PATH = "C:\\Users\\Ajay Kumar\\Downloads\\jenkin-key.pem"
                    def EC2_DEPLOY_DIR = "C:\\Apps\\api-gateway"

                    echo "Deploying API Gateway to EC2 at ${EC2_HOST}..."

                    // ✅ Stop any existing Java process remotely (optional)
                    bat """
                    echo Stopping existing API Gateway on EC2...
                    ssh -i "${PEM_PATH}" ${EC2_USER}@${EC2_HOST} "taskkill /F /IM java.exe || exit 0"
                    """

                    // ✅ Copy new JAR file to EC2
                    bat """
                    echo Copying JAR to EC2...
                    scp -i "${PEM_PATH}" target\\api-gateway-0.0.1-SNAPSHOT.jar ${EC2_USER}@${EC2_HOST}:/C:/Apps/api-gateway/
                    """

                    // ✅ Start new API Gateway process on EC2
                    bat """
                    echo Starting API Gateway on EC2...
                    ssh -i "${PEM_PATH}" ${EC2_USER}@${EC2_HOST} "cd C:\\Apps\\api-gateway && start cmd /c java -jar api-gateway-0.0.1-SNAPSHOT.jar --server.port=8765"
                    """
                }
            }
        }
    }

    post {
        always {
            echo "API Gateway deployment finished."
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
