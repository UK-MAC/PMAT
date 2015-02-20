/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.awe.pmat.gui.events;

import uk.co.awe.pmat.gui.events.EventHub.EventType;

/**
 * 
 * @author AWE Plc copyright 2013
 */
public interface EventListener {

	void receiveEvent(EventType eventType);

}
