/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.lucko.luckperms.fabric.model.IServerCommandSource;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.FutureQueue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin to store the original command source entity
 */
@Mixin(ServerCommandSource.class)
public abstract class ServerCommandSourceMixin implements IServerCommandSource {

    @Unique
    @Nullable
    private Entity luckperms$originalEntity;

    @Override
    public void luckperms$setOriginalEntity(Entity originalEntity) {
        this.luckperms$originalEntity = originalEntity;
    }

    @Override
    public @Nullable Entity luckperms$getOriginalEntity() {
        return this.luckperms$originalEntity;
    }

    @WrapOperation(
        method = "withEntity",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/server/command/CommandOutput;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec2f;Lnet/minecraft/server/world/ServerWorld;ILjava/lang/String;Lnet/minecraft/text/Text;Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/entity/Entity;ZLnet/minecraft/command/ReturnValueConsumer;Lnet/minecraft/command/argument/EntityAnchorArgumentType$EntityAnchor;Lnet/minecraft/network/message/SignedCommandArguments;Lnet/minecraft/util/thread/FutureQueue;)Lnet/minecraft/server/command/ServerCommandSource;"
        )
    )
    public ServerCommandSource luckperms$copyOriginalEntity(CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, Entity entity, boolean silent, ReturnValueConsumer resultStorer, EntityAnchorArgumentType.EntityAnchor entityAnchor, SignedCommandArguments signedArguments, FutureQueue messageChainTaskQueue, Operation<ServerCommandSource> original) {
        ServerCommandSource copy = original.call(output, pos, rot, world, level, name, displayName, server, entity, silent, resultStorer, entityAnchor, signedArguments, messageChainTaskQueue);
        ((IServerCommandSource) copy).luckperms$setOriginalEntity(this.luckperms$originalEntity);
        return copy;
    }

    @ModifyReturnValue(
        method = {
            "withEntity",
            "withEntityAnchor",
            "withLevel",
            "withOutput",
            "withPosition",
            "withLookingAt(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withLookingAt(Lnet/minecraft/entity/Entity;Lnet/minecraft/command/argument/EntityAnchorArgumentType$EntityAnchor;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withMaxLevel",
            "withReturnValueConsumer(Lnet/minecraft/command/ReturnValueConsumer;)Lnet/minecraft/server/command/ServerCommandSource;",
            "withRotation",
            "withSignedArguments",
            "withSilent",
            "withWorld"
        },
        at = @At("TAIL")
    )
    public ServerCommandSource luckperms$copyOriginalEntity(ServerCommandSource copy) {
        ((IServerCommandSource) copy).luckperms$setOriginalEntity(this.luckperms$originalEntity);
        return copy;
    }
}
