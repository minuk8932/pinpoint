/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.akka.http.interceptor;

import akka.http.scaladsl.marshalling.ToResponseMarshallable;
import akka.http.scaladsl.model.StatusCodes;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.plugin.akka.http.AkkaHttpConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import scala.Tuple2;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class RequestContextImplCompleteInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private SpanEventRecorder recorder;

    @Mock
    private ToResponseMarshallable marshallable;

    private RequestContextImplCompleteInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        Tuple2 tuple2 = Tuple2.apply(StatusCodes.OK(), "EMPTY");
        doReturn(tuple2).when(marshallable).value();
        interceptor = new RequestContextImplCompleteInterceptor(traceContext, descriptor);
    }

    @Test
    public void doInBeforeTrace() {
        interceptor.doInBeforeTrace(recorder, null, null, new Object[]{marshallable});
        verify(recorder).recordAttribute(AnnotationKey.HTTP_STATUS_CODE, StatusCodes.OK().intValue());
    }

    @Test
    public void doInAfterTrace() {
        interceptor.doInAfterTrace(recorder, null, null, null, null);
        verify(recorder).recordApi(descriptor);
        verify(recorder).recordServiceType(AkkaHttpConstants.AKKA_HTTP_SERVER_INTERNAL);
        verify(recorder).recordException(null);
    }
}