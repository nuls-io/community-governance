/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.contract.func.council;

import java.math.BigInteger;

/**
 * @author: Charlie
 * @date: 2019/8/14
 */
public interface CouncilConfig {

    /** 管理*/
    int MANAGEMENT_TYPE = 1;
    /**
     * 运营理事
     */
    int OPERATIONS_TYPE = 2;
    /**
     * 技术理事
     */
    int TECHNOLOGY_TYPE = 3;

    int MANAGEMENT_MEMBERS = 3;
    int OPERATIONS_MEMBERS = 4;
    int TECHNOLOGY_MEMBERS = 4;


    int COUNCIL_MEMBERS = 11;
    BigInteger TECHNOLOGY_ENTRY_MINIMUM = new BigInteger("2500000000000");
    BigInteger NON_TECHNOLOGY_ENTRY_MINIMUM = new BigInteger("5000000000000");
}
