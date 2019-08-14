/*
 * Copyright (C) 2018 — 2019 Honerfor, Inc.
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

package org.h1r4.commons.util.function;

/**
 * <p>
 * Represents an operation that returns a condition/boolean.
 * whose functional method is {@link #isMet()}.
 * </p>
 *
 * @author B0BAI
 * @since 1.0
 */
@FunctionalInterface
public interface Condition {

    /**
     * <p>Check the condition</p>
     *
     * @return the final condition of boolean
     */
    boolean isMet();
}