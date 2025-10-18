pipeline {
    agent any
 
    environment {
        DEPLOY_DIR = "/home/ec2-user/api-gateway"
        EC2_HOST = "13.61.25.51"
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

        echo ===== Killing any old process using port 8085 =====
        ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "
            sudo fuser -k 8085/tcp || true
            sleep 3
        "

        echo ===== Cleaning old log =====
        ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "> ${DEPLOY_DIR}/${SERVICE_NAME}.log"

        echo ===== Starting new API-Gateway instance =====
        ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "
            nohup java -jar ${DEPLOY_DIR}/${SERVICE_NAME}.jar --server.port=8085 > ${DEPLOY_DIR}/${SERVICE_NAME}.log 2>&1 &
        "

        echo ===== Waiting and checking if API-Gateway started =====
        ssh -i "${PEM_PATH}" -o StrictHostKeyChecking=no ec2-user@${EC2_HOST} "sleep 10 && curl -s http://localhost:8085/actuator/health || echo 'API-Gateway may not be up yet'"
        
        echo ✅ Deployment completed successfully!
        """
    }
}


    }
 
    post {
        failure {
            echo "❌ Deployment failed. Check Jenkins console logs for details."
        }
    }
}
