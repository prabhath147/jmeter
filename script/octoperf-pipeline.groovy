#!/usr/bin/env groovy
node {

    stage('Initialise') {
        /* Checkout the scripts */
        checkout scm: [
                $class: 'GitSCM',
                userRemoteConfigs: [
                        [
                                url: "https://github.com/prabhath147/jmeter.git",
                                credentialsId: "dc6211ae-b8a3-4519-9940-f85375ee3fe3"
                        ]
                ],
                branches: [[name: "main"]]
        ], poll: false
    }

	stage('Jmeter'){
		
			echo 'Jmeter'
		
	}
	
	stage('Jmeter test file'){
		
			dir("${WORKSPACE}\\test_plan") {
			//bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\jmeter.bat -n -t Test_Plan3.jmx -l C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl'
			 }
		
	}
	stage('test response time result'){
		
			script{
			perfReport errorFailedThreshold: 1, errorUnstableResponseTimeThreshold: 'Shift-Left.jtl:20', filterRegex: '', modePerformancePerTestCase: true, showTrendGraphs: true, sourceDataFiles: 'C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl'
			echo "${currentBuild.result}"
			}
		
	}
	stage('Jmeter generate csv'){
		
			script{			
			//bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\test1.csv'
			bat 'C:\\Training\\Jmeter\\apache-jmeter-5.5\\bin\\JMeterPluginsCMD.bat --generate-csv C:\\Training\\Jmeter\\jmeter\\test_plan\\test1.csv --input-jtl C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl --plugin-type SynthesisReport'
			
			}
		
	}
	stage('test throughput value'){
		
			script{
			
				def records = readFile file: 'C:\\Training\\Jmeter\\jmeter\\test_plan\\test1.csv'
				def lines=records.readLines()
			   
				def lastline=lines.get(lines.size()-1).split(",")    
				  
				echo "${lastline[8]}"
				float f = Float.valueOf(lastline[8]); 
				echo "${f}"
				if(f<3){
				  currentBuild.result='Failure'
				}
			}
	}
	stage('delete Jmeter files'){
		
			dir("${WORKSPACE}\\test_plan") {
			bat 'cd'
			bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\Shift-Left.jtl'
			bat 'del C:\\Training\\Jmeter\\jmeter\\test_plan\\test1.csv'
			 }
		
	}
}
