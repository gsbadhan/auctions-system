package com.auctions.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.AuthMode;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.InfoPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;

@Configuration
@PropertySource("classpath:application.properties")
public class BeanConfigs {
	private static final Logger logger = LoggerFactory.getLogger(BeanConfigs.class);

	@Autowired
	private Environment env;

	@Bean
	@Qualifier("aerospikeClient")
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public AerospikeClient aerospikeClient() {
		ClientPolicy policy = new ClientPolicy();
		policy.user = env.getProperty("aerospike.user");
		policy.password = env.getProperty("aerospike.password");
		policy.authMode = AuthMode.INTERNAL;
		policy.readPolicyDefault = new Policy();
		policy.writePolicyDefault = new WritePolicy();
		policy.scanPolicyDefault = new ScanPolicy();
		policy.queryPolicyDefault = new QueryPolicy();
		policy.batchPolicyDefault = new BatchPolicy();
		policy.infoPolicyDefault = new InfoPolicy();
		policy.timeout = Integer.parseInt(env.getProperty("aerospike.default.timeout.ms"));
		policy.maxConnsPerNode = Integer.parseInt(env.getProperty("aerospike.per.node.connections"));
		Host[] hosts = Host.parseHosts(env.getProperty("aerospike.host"),
				Integer.parseInt(env.getProperty("aerospike.port")));
		AerospikeClient client = new AerospikeClient(policy, hosts);
		client.getNodeNames().forEach(s -> logger.info("aerospike node name:" + s));
		return client;
	}

}
