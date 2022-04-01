/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saltarelli.journey.output;

import com.saltarelli.journey.parsing.ParserOutput;

/**
 *
 * @author Alberto
 */
public interface ParserOutputHandler {
    Boolean hasTextOutput(ParserOutput output);
    String textOutput(ParserOutput output);
}
