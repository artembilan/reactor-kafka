/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package reactor.kafka.tools.perf;

import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import reactor.kafka.AbstractKafkaTest;
import reactor.kafka.tools.perf.ProducerPerformance.NonReactiveProducerPerformance;
import reactor.kafka.tools.perf.ProducerPerformance.ReactiveProducerPerformance;
import reactor.kafka.tools.perf.ProducerPerformance.Stats;
import reactor.kafka.tools.util.PerfTestUtils;

public class ProducerPerformanceTest extends AbstractKafkaTest {

    private int numMessages;
    private int messageSize;
    private int maxPercentDiff;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        numMessages = PerfTestUtils.getTestConfig("reactor.kafka.test.numMessages", 5000000);
        messageSize = PerfTestUtils.getTestConfig("reactor.kafka.test.messageSize", 100);
        maxPercentDiff = PerfTestUtils.getTestConfig("reactor.kafka.test.maxPercentDiff", 50);
    }

    @Test
    public void performanceRegressionTest() throws Exception {

        NonReactiveProducerPerformance nonReactive = new NonReactiveProducerPerformance(producerProps(), topic, numMessages, messageSize, -1);
        Stats nrStats = nonReactive.runTest();
        nrStats.printTotal();
        assertEquals(numMessages, (int) nrStats.count());

        ReactiveProducerPerformance reactive = new ReactiveProducerPerformance(producerProps(), topic, numMessages, messageSize, -1);
        Stats rStats = reactive.runTest();
        rStats.printTotal();
        assertEquals(numMessages, (int) rStats.count());

        PerfTestUtils.verifyReactiveThroughput(rStats.recordsPerSec(), nrStats.recordsPerSec(), maxPercentDiff);
        // PerfTestUtils.verifyReactiveLatency(rStats.percentiles(0.75)[0], nrStats.percentiles(0.75)[0], 100);
    }

    private Map<String, Object> producerProps() {
        return PerfTestUtils.producerProps(embeddedKafka);
    }
}