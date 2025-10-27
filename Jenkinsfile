pipeline {
    agent any
 
    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "ec2-16-16-176-137.eu-north-1.compute.amazonaws.com"
        SERVICE_NAME = "api-gateway"
        PEM_PATH = "C:\\Users\\KRISHNA\\Downloads\\ec2-linux-key.pem"
    }
 
    tools {
        jdk 'Java17'
        maven 'Maven3'
    }
 
    stages {
 
        stage('Checkout') {
            steps {
                git url: 'https://github.com/Pransquare/Api-Gateway.git', branch: 'master'
            }
        }
 
        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
 
        stage('Deploy to EC2') {
            steps {
                bat """
                echo ===== Creating deploy directory on EC2 if not exists =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "mkdir -p ${DEPLOY_DIR}"
 
                echo ===== Copying JAR to EC2 =====
                scp -i "${PEM_PATH}" -o StrictHostKeyChecking=no target\\${SERVICE_NAME}.jar ec2-user@${EC2_HOST}:${DEPLOY_DIR}/
 
                echo ===== Stopping old API-Gateway instance if running =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "pkill -f ${SERVICE_NAME}.jar || true"
 
                echo ===== Starting new API-Gateway instance =====
                ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=8085 > ${DEPLOY_DIR}/api-gateway.log 2>&1 &"
 
                echo  Deployment completed successfully!
                """
            }
        }
    }
 
    post {
        failure {
            echo " Deployment failed. Check Jenkins console logs for details."
        }
    }
}
