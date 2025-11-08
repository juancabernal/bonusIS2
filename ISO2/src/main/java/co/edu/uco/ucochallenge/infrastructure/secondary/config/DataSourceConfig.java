package co.edu.uco.ucochallenge.infrastructure.secondary.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import co.edu.uco.ucochallenge.crosscutting.secret.SecretProvider;

@Configuration
public class DataSourceConfig {

  private final SecretProvider secrets;

  public DataSourceConfig(SecretProvider secrets) {
    this.secrets = secrets;
  }

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  // nombres de los secretos en el Key Vault
  private static final String SECRET_DB_USERNAME = "db-username";
  private static final String SECRET_DB_PASSWORD = "db-password";

  @Primary
  @Bean
  public DataSource dataSource() {
    String username = secrets.get(SECRET_DB_USERNAME);
    String password = secrets.get(SECRET_DB_PASSWORD);

    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(url);
    cfg.setUsername(username);
    cfg.setPassword(password);
    cfg.setDriverClassName(driverClassName);
    cfg.setMaximumPoolSize(10);
    cfg.setMinimumIdle(1);
    cfg.setPoolName("UcoHikariPool");

    return new HikariDataSource(cfg);
  }
}
