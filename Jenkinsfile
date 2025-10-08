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

        stage('Deploy') {
            steps {
                echo "Deploying API Gateway..."
                // Stop previous API Gateway process if running
                bat 'taskkill /F /IM java.exe || exit 0'
                // Create deploy folder if it does not exist
                bat "if not exist %DEPLOY_DIR% mkdir %DEPLOY_DIR%"
                // Copy JAR to deploy folder
                bat "xcopy target\\*.jar %DEPLOY_DIR% /Y /Q"
                // Start API Gateway
                bat "start cmd /c java -jar %DEPLOY_DIR%\\%SERVICE_NAME%.jar --server.port=%SERVICE_PORT%"
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
