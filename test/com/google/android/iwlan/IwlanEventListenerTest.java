/*
 * Copyright 2020 The Android Open Source Project
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

package com.google.android.iwlan;

import static com.android.dx.mockito.inline.extended.ExtendedMockito.mockitoSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;

import java.util.*;

public class IwlanEventListenerTest {
    private static final String TAG = "IwlanEventListenerTest";

    @Mock private Context mMockContext;
    @Mock private Handler mMockHandler;
    @Mock private Message mMockMessage;
    @Mock private WifiManager mMockWifiManager;
    @Mock private WifiInfo mMockWifiInfo;

    private static final int DEFAULT_SLOT_INDEX = 0;
    private static final String WIFI_SSID_1 = "TEST_AP_NAME_1";
    private static final String WIFI_SSID_2 = "TEST_AP_NAME_2";
    private IwlanEventListener mIwlanEventListener;
    private List<Integer> events;

    MockitoSession mStaticMockSession;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mStaticMockSession = mockitoSession().startMocking();

        when(mMockContext.getSystemService(eq(WifiManager.class))).thenReturn(mMockWifiManager);

        when(mMockWifiManager.getConnectionInfo()).thenReturn(mMockWifiInfo);

        mIwlanEventListener = IwlanEventListener.getInstance(mMockContext, DEFAULT_SLOT_INDEX);
    }

    @After
    public void cleanUp() throws Exception {
        mStaticMockSession.finishMocking();
    }

    @Test
    public void testWifiApChanged() throws Exception {
        when(mMockHandler.obtainMessage(eq(IwlanEventListener.WIFI_AP_CHANGED_EVENT)))
                .thenReturn(mMockMessage);

        events = new ArrayList<Integer>();
        events.add(IwlanEventListener.WIFI_AP_CHANGED_EVENT);
        mIwlanEventListener.addEventListener(events, mMockHandler);

        // First Wifi connected should not trigger WIFI_AP_CHANGED_EVENT
        when(mMockWifiInfo.getSSID()).thenReturn(WIFI_SSID_1);
        mIwlanEventListener.onWifiConnected(mMockContext);
        verify(mMockMessage, times(0)).sendToTarget();

        when(mMockWifiInfo.getSSID()).thenReturn(WIFI_SSID_2);
        mIwlanEventListener.onWifiConnected(mMockContext);
        verify(mMockMessage, times(1)).sendToTarget();
    }
}
