package cn.ipman.config.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/12 21:00
 */
@Component
@Slf4j
public class DistributedLocks {

    @Autowired
    DataSource dataSource;

    Connection connection;

    @Getter
    private final AtomicBoolean locked = new AtomicBoolean(false);

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        executor.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    private void tryLock() {
        try {
            lock();
            locked.set(true);
        } catch (Exception ex) {
            log.info(" ==>>>> unable to get lock.");
            locked.set(false);
        }
    }

    public void lock() throws SQLException {
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection.createStatement().execute("set innodb_lock_wait_timeout=5");
        connection.createStatement().execute("select app from locks where id = 1 for update");

        if (locked.get()) {
            log.info(" ==>>>> reenter this dist lock.");
        } else {
            log.info(" ==>>>> get a dist lock.");
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.close();
            }
        } catch (SQLException e) {
            log.error("close connection error");
        }
    }


}
