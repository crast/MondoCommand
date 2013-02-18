package us.crast.MondoCommandExample;

import mondocommand.CallInfo;
import mondocommand.MondoFailure;
import mondocommand.SubHandler;

public class HouseBuildHandler implements SubHandler {

    @Override
    public void handle(CallInfo call) throws MondoFailure {
        call.append("Building house with name %s for %s", call.getArg(0), call.getArg(1));
    }

}
