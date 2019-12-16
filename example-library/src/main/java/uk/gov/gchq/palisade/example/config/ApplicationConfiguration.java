/*
 * Copyright 2019 Crown Copyright
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.gchq.palisade.example.rule.BankDetailsRule;
import uk.gov.gchq.palisade.example.rule.DutyOfCareRule;
import uk.gov.gchq.palisade.example.rule.FirstResourceRule;
import uk.gov.gchq.palisade.example.rule.NationalityRule;
import uk.gov.gchq.palisade.example.rule.RecordMaskingRule;
import uk.gov.gchq.palisade.example.rule.ZipCodeMaskingRule;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public BankDetailsRule bankDetailsRule() {
        return new BankDetailsRule();
    }

    @Bean
    public DutyOfCareRule dutyOfCareRule() {
        return new DutyOfCareRule();
    }

    @Bean
    public FirstResourceRule firstResourceRule() {
        return new FirstResourceRule();
    }

    @Bean
    public NationalityRule nationalityRule() {
        return new NationalityRule();
    }

    @Bean
    public RecordMaskingRule recordMaskingRule() {
        return new RecordMaskingRule();
    }

    @Bean
    public ZipCodeMaskingRule zipCodeMaskingRule() {
        return new ZipCodeMaskingRule();
    }
}
