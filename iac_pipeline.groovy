// Description pipeline
pipeline {
  agent any
  stages {
    stage('Submit Stack') { 
      steps {
          catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'aliTestJenkinsUserCred01', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY']]) {
              sh "aws cloudformation deploy --template-file  $workspace/cloudformation/TrainingEvent-ApacheTomCatServer.json --stack-name aliTomCatWeb-Stack-Test --region us-east-1 --parameter-overrides InstanceType=t2.micro KeyName=myTestKeyPair02 SSHLocation=0.0.0.0/0 --tags name=TomCatWeb-Stack-Test"
              //sh "aws cloudformation create-stack --template-body '$workspace/cloudformation/TrainingEvent-UbuntuServer.py' --stack-name TomCatWeb-Stack-Val --region us-east-1 --parameters ParameterKey=InstanceType,ParameterValue=t2.micro ParameterKey=KeyName,ParameterValue='aliTrainingKeyPair2' ParameterKey=SSHLocation,ParameterValue=0.0.0.0/0"
              //sh "echo SKIPPING INFRASTRUCTURE CREATION/UPDATE for now .."
            }//end withCredentials
            sh "exit 0"
         }//end catcherror
      }
    }
    stage('Update Inventory'){
      steps{
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
           //sh "echo SKIPPING Automated update of inventory file in lieu to manual update .."
             withCredentials([sshUserPrivateKey(credentialsId: '43645869-fe60-4838-ab62-94581a9270d1', keyFileVariable: 'aliTrainingKeyPair2')]) {
            // sh 'ansible-playbook ./ansible/playbooks/update_inventory.yml --user ubuntu --key-file ${myTestKeyPair02}'  
            //  sh '/etc/ansible/ec2.py --list'
           }//end withCredentials
          sh "exit 0"
         }//end catchError
      }
    }
    stage('Configure Tomcat') {
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          withCredentials([sshUserPrivateKey(credentialsId: 'a59a13e3-8e2f-4920-83c9-a49b576e5d58', keyFileVariable: 'myKEY')]) {
         //    sh 'ansible-playbook ./ansible/playbooks/tomcat-setup.yml --user ubuntu --key-file ${myKEY}'  
           }//end withCredentials
          sh "exit 0"
         }//end catchError
      }//end steps
    } //end stage
  } //end stages
}//end pipeline