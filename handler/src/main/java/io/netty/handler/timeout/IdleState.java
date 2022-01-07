/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.timeout;

import io.netty.channel.Channel;


/**
 * An {@link Enum} that represents the idle state of a {@link Channel}.
 */
public enum IdleState {
    /** [neo] 一段时间没有收到数据
     * No data was received for a while.
     */
    READER_IDLE,
    /** [neo] 一段时间没有发送数据
     * No data was sent for a while.
     */
    WRITER_IDLE,
    /** [neo] 一段时间没有收到或发送数据
     * No data was either received or sent for a while.
     */
    ALL_IDLE
}
