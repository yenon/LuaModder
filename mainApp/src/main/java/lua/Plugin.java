package lua;

import org.luaj.vm2.Globals;

public interface Plugin {
    void onLoad(Globals globals);

    void clear();
}
