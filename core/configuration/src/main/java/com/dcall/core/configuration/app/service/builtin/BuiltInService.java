package com.dcall.core.configuration.app.service.builtin;

import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.generic.service.command.GenericCommandService;

public interface BuiltInService extends GenericCommandService {
    Parser getParser();
    BuiltInService setParser(final Parser parser);

}
