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
        echo "Deploying API Gateway to EC2..."

        // Define variables
        script {
            def EC2_USER = "Administrator"
            def EC2_HOST = "13.61.190.82" // your EC2 public IP
            def PEM_PATH = "C:\\Users\\swapn\\Downloads\\s-key.pem"
            def EC2_DEPLOY_DIR = "C:\\Apps\\api-gateway"
        }

        // Stop any existing process remotely
        bat """
        echo Stopping existing API Gateway on EC2...
        pscp -i "%PEM_PATH%" stop-app.bat ${EC2_USER}@${EC2_HOST}:${EC2_DEPLOY_DIR}\\
        """

        // Copy new JAR file to EC2
        bat """
        echo Copying new JAR to EC2...
        scp -i "%PEM_PATH%" target\\api-gateway-0.0.1-SNAPSHOT.jar ${EC2_USER}@${EC2_HOST}:/C:/Apps/api-gateway/
        """

        // Start the new process
        bat """
        echo Starting API Gateway on EC2...
        ssh -i "%PEM_PATH%" ${EC2_USER}@${EC2_HOST} "cd C:\\Apps\\api-gateway && start cmd /c java -jar api-gateway-0.0.1-SNAPSHOT.jar --server.port=8765"
        """
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
