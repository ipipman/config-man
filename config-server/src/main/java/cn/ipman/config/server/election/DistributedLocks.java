package cn.ipman.config.server.election;

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
 * 分布式锁管理类，用于通过数据库实现分布式锁机制。
 *
 * @Author IpMan
 * @Date 2024/5/12 21:00
 */
@Component
@Slf4j
public class DistributedLocks {

    // 数据源，用于获取数据库连接
    @Autowired
    DataSource dataSource;

    // 数据库连接。
    Connection connection;

    // 标记当前是否持有锁。
    @Getter
    private final AtomicBoolean locked = new AtomicBoolean(false);

    // 定时任务执行器，用于定时尝试获取锁。
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    /**
     * 初始化方法，于对象创建后执行。
     * 主要完成：
     * 1. 从数据源获取数据库连接。
     * 2. 定时尝试获取锁。
     */
    @PostConstruct
    public void init() {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 定时尝试获取锁，初始延迟1秒，之后每5秒尝试一次。
        executor.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);
    }


    /**
     * 尝试获取锁的逻辑。
     * 如果获取成功，则设置持有锁的状态为true。
     * 如果获取失败，则重置持有锁的状态为false。
     */
    private void tryLock() {
        try {
            lock();
            locked.set(true);
        } catch (Exception ex) {
            log.info(" ==>>>> unable to get lock.");
            locked.set(false);
        }
    }


    /**
     * 实际加锁逻辑，通过执行SQL语句在数据库中实现。
     * 主要步骤包括：
     * 1. 关闭自动提交，设置事务隔离级别。
     * 2. 设置锁等待超时时间。
     * 3. 执行加锁SQL语句。
     *
     * @throws SQLException 如果操作数据库连接或执行SQL语句时发生错误。
     */
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


    /**
     * 销毁方法，于对象销毁前执行。
     * 主要完成：
     * 1. 如果数据库连接未关闭，回滚事务并关闭连接。
     */
    @PreDestroy
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();  // 回滚事务
                connection.close();     // 关闭连接
            }
        } catch (SQLException e) {
            log.error("close connection error");
        }
    }


}
