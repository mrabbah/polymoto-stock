dataSource {
    pooled = true
    driverClassName = "org.hsqldb.jdbcDriver"
    username = "sa"
    password = ""
}
hibernate {
    //hibernate.show_sql = true
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {        
        dataSource {
            //dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            //url = "jdbc:hsqldb:mem:devDB"
            //logSql = true
            driverClassName = "org.postgresql.Driver"
            dialect = org.hibernate.dialect.PostgreSQLDialect
            username = "root"
            password = "root"
            dbCreate = "validate"
            url = "jdbc:postgresql://localhost/gs_csdrfaraj_12_11_2015"         
        }

        }
    test {
        dataSource {
            driverClassName = "org.postgresql.Driver"
            dialect = org.hibernate.dialect.PostgreSQLDialect
            username = "root"
            password = "root"
            dbCreate = "validate"
            url = "jdbc:postgresql://localhost/csdrfaraj"          
        }
    }
    production {
        dataSource {
            driverClassName = "org.postgresql.Driver"
            dialect = org.hibernate.dialect.PostgreSQLDialect
            username = "root"
            password = "root"
            dbCreate = "validate"
            jndiName = "jdbc/stock"           
        }
    }
}
