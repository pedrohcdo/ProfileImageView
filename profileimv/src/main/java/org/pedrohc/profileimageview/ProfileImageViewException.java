package org.pedrohc.profileimageview;

/**
 *  Copyright - Pedro H. Chaves
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
 *
 * Created by Pedro on 18/03/2016.
 */
final class ProfileImageViewException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor
     */
    public ProfileImageViewException() {}

    /**
     * Constructor
     */
    public ProfileImageViewException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public ProfileImageViewException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public ProfileImageViewException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}