grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.war.file = "target/${appName}.war"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        
        grailsCentral()
        grailsPlugins() 
        grailsHome() 

        mavenLocal() 
        mavenCentral() 
        mavenRepo "http://snapshots.repository.codehaus.org" 
        mavenRepo "http://repository.codehaus.org" 
        mavenRepo "http://download.java.net/maven/2/" 
        mavenRepo "http://repository.jboss.com/maven2/" 
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
    }

    checksums false
}
codenarc.reports = {
    RapportAnalyseQualiteCode('xml') {                    // The report name "MyXmlReport" is user-defined; Report type is 'xml'
        outputFile = 'CodeNarc.xml'  // Set the 'outputFile' property of the (XML) Report
        title = 'Static Analysis Rules Report'             // Set the 'title' property of the (XML) Report
    }
}
