/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.choranet.zk

import org.zkoss.zul.*;
/**
 *
 * @author rabbah
 */
class TreeNodeConverter {
	public static convertAll(list) {
            def result = []
            for(element in list) {
                result.add(new DefaultTreeNode(element))
            }
            return result
        }
        public static convert(element) {
            return new DefaultTreeNode(element)
        }
}

