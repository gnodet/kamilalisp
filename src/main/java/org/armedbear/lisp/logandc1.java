/*
 * logandc1.java
 *
 * Copyright (C) 2003-2004 Peter Graves
 * $Id$
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */

package org.armedbear.lisp;

import java.math.BigInteger;

import static org.armedbear.lisp.Lisp.number;
import static org.armedbear.lisp.Lisp.type_error;

public final class logandc1 extends Primitive
{
    private logandc1()
    {
        super("logandc1", "integer-1 integer-2");
    }

    @Override
    public LispObject execute(LispObject first, LispObject second)

    {
        if (first instanceof Fixnum) {
            if (second instanceof Fixnum)
                return Fixnum.getInstance(~((Fixnum)first).value &
                                  ((Fixnum)second).value);
            if (second instanceof Bignum) {
                BigInteger n1 = ((Fixnum)first).getBigInteger();
                BigInteger n2 = ((Bignum)second).value;
                return number(n1.not().and(n2));
            }
            return type_error(second, Symbol.INTEGER);
        }
        if (first instanceof Bignum) {
            BigInteger n1 = ((Bignum)first).value;
            if (second instanceof Fixnum) {
                BigInteger n2 = ((Fixnum)second).getBigInteger();
                return number(n1.not().and(n2));
            }
            if (second instanceof Bignum) {
                BigInteger n2 = ((Bignum)second).value;
                return number(n1.not().and(n2));
            }
            return type_error(second, Symbol.INTEGER);
        }
        return type_error(first, Symbol.INTEGER);
    }

    private static final Primitive LOGANDC1 = new logandc1();
}
