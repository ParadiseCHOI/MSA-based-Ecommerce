// package com.example.apigatewayservice.config;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.client.ServiceInstance;
// import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
// import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
// import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
// import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;
//
// // @Configuration
// public class LoadBalancingConfiguration {
//     // default: round-robin
//     private Environment env;
//     private LoadBalancerClientFactory loadBalancerClientFactory;
//
//     // @Autowired
//     // public LoadBalancingConfiguration(Environment env, LoadBalancerClientFactory loadBalancerClientFactory) {
//     //     this.env = env;
//     //     this.loadBalancerClientFactory = loadBalancerClientFactory;
//     // }
//     // @Bean
//     public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
//             Environment env, LoadBalancerClientFactory loadBalancerClientFactory) {
//         String name = loadBalancerClientFactory.getName(env);
//         return new RandomLoadBalancer(
//                 loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
//     }
//
// }
