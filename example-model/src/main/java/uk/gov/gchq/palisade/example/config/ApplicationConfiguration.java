/*
 * Copyright 2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.palisade.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.example.repository.BackingStore;
import uk.gov.gchq.palisade.example.repository.SimpleCacheService;
import uk.gov.gchq.palisade.service.CacheService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Bean configuration and dependency injection graph
 */
@Configuration
@EnableConfigurationProperties(CacheConfiguration.class)
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public CacheConfiguration cacheConfiguration() {
        return new CacheConfiguration();
    }

    @Bean
    public CacheService cacheService(final Map<String, BackingStore> backingStores) {
        return Optional.of(new SimpleCacheService()).stream().peek(cache -> {
            LOGGER.info("Cache backing implementation = {}", Objects.requireNonNull(backingStores.values().stream().findFirst().orElse(null)).getClass().getSimpleName());
            cache.backingStore(backingStores.values().stream().findFirst().orElse(null));
        }).findFirst().orElse(null);
    }
}
