package org.mazur.hater.model;

import org.mazur.hater.signals.SignalOperations;
import org.mazur.hater.signals.SignalValue;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public interface SignalModelHolder {

  SignalOperations<SignalValue> getOperations();
  
}
