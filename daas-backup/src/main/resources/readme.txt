i) Jars to be added to orient db lib folder 

1. joda-time-2.3.jar
2. jets3t-0.9.0.jar
3. httpclient-4.1.2.jar
4. httpcore-4.1.2.jar
5. commons-codec-1.4.jar
6. commons-logging-1.1.1.jar
7. daas-backup-0.0.1-SNAPSHOT.jar

----------------------------------------------------------------------
ii) Modify 'orientdb-server-config.xml' with the following xml snippet

		<handler class="com.bbytes.daas.DaasS3Backup">
            <parameters>
                <parameter value="true" name="enabled"/>
                <parameter value="24h" name="delay"/>
                <parameter value="./../backup" name="target.directory"/>
                <parameter value="${DBNAME}-${DATE:yyyyMMddHHmmss}-s3.json" name="target.fileName"/>
                <parameter value="" name="db.include"/>
                <parameter value="" name="db.exclude"/>
            </parameters>
        </handler>