package com.aichebaba.beetl.functions;

import com.aichebaba.rooftop.beetl.functions
        .NumberFunctions;
import com.aichebaba.rooftop.utils.StringUtils;
import junit.framework.TestCase;

public class NumberFunctionsTest extends TestCase{

    public void testG2K() {
        System.out.println(new NumberFunctions().g2k(10101));
    }

    public void testFormat() {
        System.out.println(StringUtils.digitFormat(1));
    }
}
