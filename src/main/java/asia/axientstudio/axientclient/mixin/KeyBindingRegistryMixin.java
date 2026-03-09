package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(GameOptions.class)
public abstract class KeyBindingRegistryMixin {

    @Accessor("allKeys")
    @Mutable
    public abstract void setAllKeys(KeyBinding[] keys);

    @Accessor("allKeys")
    public abstract KeyBinding[] getAllKeys();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        List<KeyBinding> keys = new ArrayList<>(Arrays.asList(getAllKeys()));
        keys.add(AxientClient.openMenuKey);
        keys.add(AxientClient.freelookKey);
        keys.add(AxientClient.zoomKey);
        keys.add(AxientClient.sneakToggleKey);
        keys.add(AxientClient.sprintToggleKey);
        setAllKeys(keys.toArray(new KeyBinding[0]));
    }
}
