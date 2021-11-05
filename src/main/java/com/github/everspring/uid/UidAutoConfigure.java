package com.github.everspring.uid;

import com.github.everspring.uid.impl.CachedUidGenerator;
import com.github.everspring.uid.impl.DefaultUidGenerator;
import com.github.everspring.uid.impl.UidProperties;
import com.github.everspring.uid.worker.DisposableWorkerIdAssigner;
import com.github.everspring.uid.worker.WorkerIdAssigner;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * UID 的自动配置
 *
 * @author wujun
 * @author everspring
 * @date 2019.02.20 10:57
 */
@Configuration
@ConditionalOnClass({ DefaultUidGenerator.class, CachedUidGenerator.class })
@MapperScan({ "com.github.everspring.uid.worker.dao" })
@EnableConfigurationProperties(UidProperties.class)
public class UidAutoConfigure {

	private final String CACHED_GENERATOR_IMPL = "cached";

	@Autowired
	private UidProperties uidProperties;

	@Bean
	@Lazy
	@ConditionalOnMissingBean
	UidGenerator uidGenerator() {
		String generatorImpl = uidProperties.getGeneratorImpl();
		Boolean defaultBln = true;
		if (StringUtils.isBlank(generatorImpl)) {
			defaultBln = true;
		}
		if (CACHED_GENERATOR_IMPL.equalsIgnoreCase(generatorImpl)) {
			defaultBln = false;
		}
		return defaultBln ? new DefaultUidGenerator(uidProperties) : new CachedUidGenerator(uidProperties);
	}

	@Bean
	@ConditionalOnMissingBean
    WorkerIdAssigner workerIdAssigner() {
		return new DisposableWorkerIdAssigner();
	}
}
