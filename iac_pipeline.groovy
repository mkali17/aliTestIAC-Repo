// Description pipeline
pipeline {
  agent any
  stages {
    stage('Submit Stack') { 
      steps {
          catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'aliTestJenkinsUser02', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY']]) {
              //sh "aws cloudformation deploy --template-file $workspace/cloudformation/TrainingEvent-UbuntuServer.json --stack-name TomCatWeb-Stack-Test --region us-east-1 --parameter-overrides InstanceType=t2.micro KeyName=myTestKeyPair02 SSHLocation=0.0.0.0/0 --tags name=TomCatWeb-Stack-Test"
              sh "echo SKIPPING INFRASTRUCTURE CREATION/UPDATE for now .."
            }//end withCredentials
            sh "exit 0"
         }//end catcherror
      }
    }
    stage('Update Inventory'){
      steps{
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
              withCredentials([sshUserPrivateKey(credentialsId: '43645869-fe60-4838-ab62-94581a9270d1', keyFileVariable: 'myTestKeyPair02')]) {
                sh 'ansible-playbook ./ansible/playbooks/update_inventory.yml --user ubuntu --key-file ${myTestKeyPair02}' 
           }//end withCredentials
          sh "exit 0"
         }//end catchError
      }
    }
    stage('Configure Tomcat') {
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          withCredentials([sshUserPrivateKey(credentialsId: '43645869-fe60-4838-ab62-94581a9270d1', keyFileVariable: 'myTestKeyPair02')]) {
            sh 'ansible-playbook ./ansible/playbooks/tomcat-setup.yml --user ubuntu -vvv --key-file ${myTestKeyPair02}'
            }//end withCredentials
          sh "exit 0"
         }//end catchError
      }//end steps
    } //end stage
  } //end stages
}//end pipeline