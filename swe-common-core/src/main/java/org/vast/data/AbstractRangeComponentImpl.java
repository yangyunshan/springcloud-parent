/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.data;

import net.opengis.swe.v20.SimpleComponent;
import net.opengis.swe.v20.DataBlock;


/**
 * <p>
 * Base implementation for all range data components with min/max values
 * 11-2014: Updated to implement new API autogenerated from XML schema
 * </p>
 *
 * @author Alex Robin
 * */
public abstract class AbstractRangeComponentImpl extends AbstractSimpleComponentImpl implements SimpleComponent
{
    private static final long serialVersionUID = 5987133295365471520L;
    protected DataValue min;
    protected DataValue max;
    
    
    protected AbstractRangeComponentImpl()
    {
        this.scalarCount = 2;
    }
    
    
    @Override
    public int getComponentCount()
    {
        return 2;
    }
    
    
    @Override
    public DataValue getComponent(int index)
    {
        if (index == 0)
            return min;
        
        if (index == 1)
            return max;
        
        throw new IndexOutOfBoundsException("Ranges have only two components. Index can only be 0 or 1"); 
    }
   
    
    @Override
    protected void updateStartIndex(int startIndex)
    {
        dataBlock.startIndex = startIndex;
    }
        
    
    @Override
    public void setData(DataBlock dataBlock)
    {
        assert(dataBlock != null);        
        this.dataBlock = (AbstractDataBlock)dataBlock;

        // also assign dataBlock to children
        if (dataBlock instanceof DataBlockParallel)
        {
            min.setData(((DataBlockParallel)dataBlock).blockArray[0]);
            max.setData(((DataBlockParallel)dataBlock).blockArray[1]);
        }
        else if (dataBlock instanceof DataBlockMixed)
        {
            min.setData(((DataBlockParallel)dataBlock).blockArray[0]);
            max.setData(((DataBlockParallel)dataBlock).blockArray[1]);
        }
        else if (dataBlock instanceof DataBlockTuple)
        {
            AbstractDataBlock childBlock;
            
            childBlock = ((AbstractDataBlock)dataBlock).copy();
            childBlock.atomCount = 1;
            min.setData(childBlock);
            
            childBlock = ((AbstractDataBlock)dataBlock).copy();
            childBlock.atomCount = 1;
            childBlock.startIndex += 1;
            max.setData(childBlock);
        }
        else // case of big primitive array
        {
            AbstractDataBlock childBlock;
            
            childBlock = ((AbstractDataBlock)dataBlock).copy();
            childBlock.atomCount = 1;
            min.setData(childBlock);
            
            childBlock = ((AbstractDataBlock)dataBlock).copy();
            childBlock.atomCount = 1;
            childBlock.startIndex += 1;
            max.setData(childBlock);
        }
    }
    
    
    @Override
    public AbstractDataBlock createDataBlock()
    {
    	switch (dataType)
        {
        	case BOOLEAN:
        		return new DataBlockBoolean(2);
            
        	case BYTE:
        		return new DataBlockByte(2);
                
            case UBYTE:
                return new DataBlockUByte(2);
                
            case SHORT:
            	return new DataBlockShort(2);
                
            case USHORT:
                return new DataBlockUShort(2);
                
            case INT:
            	return new DataBlockInt(2);
                
            case UINT:
                return new DataBlockUInt(2);
                
            case LONG:
            case ULONG:
            	return new DataBlockLong(2);
                                
            case FLOAT:
            	return new DataBlockFloat(2);
                
            case DOUBLE:
            	return new DataBlockDouble(2);
                
            case UTF_STRING:
            case ASCII_STRING:
            	return new DataBlockString(2);
                
            default:
            	throw new IllegalStateException("Unsupported data type " + dataType);
        }
    }
}
