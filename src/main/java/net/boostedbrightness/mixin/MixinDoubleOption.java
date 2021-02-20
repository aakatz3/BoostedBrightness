package net.boostedbrightness.mixin;

import net.boostedbrightness.BoostedBrightness;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoubleOption.class)
public class MixinDoubleOption
{
	@Shadow @Final @Mutable
	private BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;

	@Shadow
	private double min;

	@Shadow
	private double max;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void init(String key, double min, double max, float step, Function<GameOptions, Double> getter,
					  BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, DoubleOption, Text> displayStringGetter,
					  CallbackInfo info)
	{
		// Modifies the max, min, and displayStringGetter of the brightness slider
		if (key.equals("options.gamma"))
		{
			this.min = BoostedBrightness.MIN_BRIGHTNESS;
			this.max = BoostedBrightness.MAX_BRIGHTNESS;
			this.displayStringGetter = this::displayStringGetter;
		}
	}

	private Text displayStringGetter(GameOptions gameOptions, DoubleOption doubleOption)
	{
		MutableText text = new TranslatableText("options.gamma").append(": ");
		double gamma = gameOptions.gamma;

		if (Math.abs(gamma) <= 0.025) {
			text.append(new TranslatableText("options.gamma.min"));
		} else if (Math.abs(gamma - 1) <= 0.025) {
			text.append(new TranslatableText("options.gamma.max"));
		} else {
			text.append(Math.round(gameOptions.gamma * 100) + "%");
		}

		return text;		   
	}
}