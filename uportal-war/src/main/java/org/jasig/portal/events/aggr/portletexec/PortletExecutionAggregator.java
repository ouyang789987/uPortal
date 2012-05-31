/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.events.aggr.portletexec;

import org.jasig.portal.events.PortalEvent;
import org.jasig.portal.events.PortletExecutionEvent;
import org.jasig.portal.events.aggr.AggregationInterval;
import org.jasig.portal.events.aggr.AggregationIntervalInfo;
import org.jasig.portal.events.aggr.BaseAggregationPrivateDao;
import org.jasig.portal.events.aggr.BasePortalEventAggregator;
import org.jasig.portal.events.aggr.DateDimension;
import org.jasig.portal.events.aggr.TimeDimension;
import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;
import org.jasig.portal.events.aggr.portletexec.PortletExecutionAggregationKey.ExecutionType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Event aggregator that uses {@link PortletExecutionAggregationPrivateDao} to aggregate portlet executions 
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class PortletExecutionAggregator extends BasePortalEventAggregator<PortletExecutionEvent, PortletExecutionAggregationImpl, PortletExecutionAggregationKey> {
    private PortletExecutionAggregationPrivateDao portletExecutionAggregationDao;
    private ExecutionType executionType = ExecutionType.ALL;

    @Autowired
    public void setPortletExecutionAggregationDao(PortletExecutionAggregationPrivateDao portletExecutionAggregationDao) {
        this.portletExecutionAggregationDao = portletExecutionAggregationDao;
    }

    /**
     * Set the type of portlet execution this aggregator works on. 
     */
    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    @Override
    public boolean supports(Class<? extends PortalEvent> type) {
        return executionType.supports(type);
    }

    @Override
    protected BaseAggregationPrivateDao<PortletExecutionAggregationImpl, PortletExecutionAggregationKey> getAggregationDao() {
        return this.portletExecutionAggregationDao;
    }

    @Override
    protected void updateAggregation(PortletExecutionEvent e, AggregationIntervalInfo intervalInfo, PortletExecutionAggregationImpl aggregation) {
        final long executionTime = e.getExecutionTimeNano();
        final int duration = intervalInfo.getDurationTo(e.getTimestampAsDate());
        aggregation.setDuration(duration);
        aggregation.addValue(executionTime);
    }

    @Override
    protected PortletExecutionAggregationKey createAggregationKey(AggregationIntervalInfo intervalInfo,
            AggregatedGroupMapping aggregatedGroup, PortletExecutionEvent event) {
        
        final TimeDimension timeDimension = intervalInfo.getTimeDimension();
        final DateDimension dateDimension = intervalInfo.getDateDimension();
        final AggregationInterval aggregationInterval = intervalInfo.getAggregationInterval();

        final String fname = event.getFname();
        
        return new PortletExecutionAggregationKeyImpl(dateDimension, timeDimension, aggregationInterval, aggregatedGroup, fname, executionType);
    }
}