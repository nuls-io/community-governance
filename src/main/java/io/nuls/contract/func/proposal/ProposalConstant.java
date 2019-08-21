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

package io.nuls.contract.func.proposal;

import java.math.BigInteger;

/**
 * @author: Charlie
 * @date: 2019/8/15
 */
public interface ProposalConstant {

    /** 角色*/
    int ROLE = 1;
    /** 系统参数*/
    int SYS_PARAM = 2;
    /** 社区基金*/
    int COMMUNITY_FUND = 3;
    /** 其他类型*/
    int OTHER_TYPE = 4;

    /** 支持*/
    int FAVOUR = 1;
    /** 反对*/
    int AGAINST = 2;
    /** 弃权*/
    int ABSTENTION  = 3;

    /** 审核通过*/
    int YES = 1;
    /** 审核拒接*/
    int NO = 0;

    /** 审核中*/
    int INREVIEW = 1;
    /** 审核拒绝*/
    int UNAPPROVED = 2;
    /** 投票中*/
    int VOTING = 3;
//    /** 通过*/
//    int ADOPTED = 4;
//    /** 未通过*/
//    int REJECTED = 4;

    /** 押金100NULS*/
    BigInteger RECOGNIZANCE = new BigInteger("10000000000");

    long DAY15_SECONDS = 86400 * 15;
}
