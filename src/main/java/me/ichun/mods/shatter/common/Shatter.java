package me.ichun.mods.shatter.common;

import me.ichun.mods.shatter.client.core.EventHandler;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.render.RenderShattered;
import me.ichun.mods.shatter.common.core.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Shatter.MOD_ID)
public class Shatter
{
    public static final String MOD_ID = "shatter";
    public static final String MOD_NAME = "Shatter";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Config config;

    public static EventHandler eventHandler;

    public Shatter()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            config = new Config().init();

            MinecraftForge.EVENT_BUS.register(eventHandler = new EventHandler());
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            EntityTypes.REGISTRY.register(bus);
            bus.addListener(this::onClientSetup);
        });
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.log(Level.ERROR, "You are loading " + MOD_NAME + " on a server. " + MOD_NAME + " is a client only mod!"));
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypes.SHATTERED.get(), new RenderShattered.RenderFactory());
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> me.ichun.mods.ichunutil.client.core.EventHandlerClient::getConfigGui);
    }

    public static class EntityTypes
    {
        private static final DeferredRegister<EntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.ENTITIES, MOD_ID);

        public static final RegistryObject<EntityType<EntityShattered>> SHATTERED = REGISTRY.register("shattered", () -> EntityType.Builder.create(EntityShattered::new, EntityClassification.MISC)
                .size(0.1F, 0.1F)
                .disableSerialization()
                .disableSummoning()
                .immuneToFire()
                .build("from " + MOD_NAME + ". Ignore this.")
        );
    }
}
