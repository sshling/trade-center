package cn.shaolingweb.rml.tradecenter.demo.guava;

import cn.shaolingweb.rml.tradecenter.service.auth.RoleService;
import cn.shaolingweb.rml.tradecenter.service.auth.UserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 权限缓存: 基于JVM实例的,类redis缓存
 */
//@Component
public class PrivilegeCache {

    private final static Logger logger = LoggerFactory.getLogger(PrivilegeCache.class);
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    private final static int refreshIntervalMins = 10;
    private static PrivilegeCache cache;

    @PostConstruct
    public void init() {
        cache = this;
    }

    private static final LoadingCache<String, List<String>> MODEL_PRIVILEGES_CACHE = CacheBuilder
            .newBuilder().concurrencyLevel(10)//支持的并发更新数
            .expireAfterWrite(refreshIntervalMins, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<String>>() {
                public List<String> load(String pin) { //HACK:内部类访问局部变量
                    return cache.roleService.findModelPrivileges(pin);
                }
            });

    private static final LoadingCache<String, List<Integer>> CLUSTER_PRIVILEGES_CACHE = CacheBuilder
            .newBuilder().concurrencyLevel(10)
            .expireAfterWrite(refreshIntervalMins, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<Integer>>() {
                public List<Integer> load(String pin) {
                    return cache.roleService.findClustersByErp(pin);
                }
            });
    private static final LoadingCache<String, List<Integer>> INDICES_PRIVILEGES_CACHE = CacheBuilder
            .newBuilder().concurrencyLevel(10)
            .expireAfterWrite(refreshIntervalMins, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<Integer>>() {
                public List<Integer> load(String pin) {
                    return cache.roleService.findIndicesByErp(pin);
                }
            });

    private static final LoadingCache<String, Boolean> SUPERADMIN_CACHE = CacheBuilder
            .newBuilder().concurrencyLevel(10)
            .expireAfterWrite(refreshIntervalMins, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Boolean>() {
                public Boolean load(String pin) {
                    return cache.userService.isSuperAdmin(pin);
                }
            });


    public static List<String> getModelPrivileges(String pin) throws ExecutionException {
        return MODEL_PRIVILEGES_CACHE.get(pin);
    }

    public static void refreshSuperAdmin(String pin) {
        SUPERADMIN_CACHE.refresh(pin);
    }

    public static boolean isSuperAdmin(String pin) throws ExecutionException {
        return SUPERADMIN_CACHE.get(pin);
    }


    public static void removeCache(String pin) {
        try {
            MODEL_PRIVILEGES_CACHE.invalidate(pin);
            SUPERADMIN_CACHE.invalidate(pin);
            CLUSTER_PRIVILEGES_CACHE.invalidate(pin);
            INDICES_PRIVILEGES_CACHE.invalidate(pin);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static void refresh(String pin) {
        try {
            MODEL_PRIVILEGES_CACHE.refresh(pin);
            SUPERADMIN_CACHE.refresh(pin);
            CLUSTER_PRIVILEGES_CACHE.refresh(pin);
            INDICES_PRIVILEGES_CACHE.refresh(pin);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static void refreshAll(String[] pins) {
        for (String pin : pins) {
            try {
                refresh(pin);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    public static void cleanAll() {
        try {
            MODEL_PRIVILEGES_CACHE.invalidateAll();
            SUPERADMIN_CACHE.invalidateAll();
            CLUSTER_PRIVILEGES_CACHE.invalidateAll();
            INDICES_PRIVILEGES_CACHE.invalidateAll();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
