package se.puggan.factory.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;
import java.util.Stack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import se.puggan.factory.container.slot.ItemSlot;
import se.puggan.factory.container.slot.ReceiptSlot;

public class FactoryScreen extends ContainerScreen<FactoryContainer> {
    /**
     * name: x, y, w, h.
     * background: 0, 0, 176, 166
     * slot off: 151, 16, 18, 18
     * slot on: 152, 83, 18, 18
     * Green button: 177, 0, 20, 18
     * Green pressed button: 177, 19, 20, 18
     * Red button: 198, 0, 20, 18
     * Red pressed button: 198, 19, 20, 18
     */
    public static final ResourceLocation GUI_MAP = new ResourceLocation("factory:textures/gui/factory_map.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();

    private boolean enabled;
    private final FactoryContainer fContainer;
    private EnabledButton enabledButton;
    private ImageButton recipeButton;
    private final int rButtonX = 150;
    private final int rButtonY = 33;
    private final int eButtonX = 60;
    private final int eButtonY = 33;

    public FactoryScreen(FactoryContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        fContainer = container;
        fContainer.addScreen(this);
        enabled = fContainer.isEnabled();
    }

    public void recipeToggle(Button button) {
        recipeBookGui.initSearchBar(false);
        recipeBookGui.toggleVisibility();
        //guiLeft = recipeBookGui.updateScreenPosition(false, width, xSize); #MCP
        guiLeft = recipeBookGui.updateScreenPosition(false, field_238742_p_, xSize);
        rePositionButtons();
    }

    public void rePositionButtons() {
        recipeButton.setPosition(guiLeft + rButtonX, guiTop + rButtonY);
        enabledButton.setPosition(guiLeft + eButtonX, guiTop + eButtonY);
    }

    public void enable() {
        enabled = true;
        enabledButton.enabled = true;
    }

    public void disable() {
        enabled = false;
        enabledButton.enabled = false;
    }

    @Override
    //protected void init() { #MCP
    protected void func_231160_c_() {
        //super.init(); #MCP
        super.func_231160_c_();
        enabledButton = new EnabledButton(
                eButtonX,
                eButtonY,
                enabled,
                fContainer::activate,
                fContainer::deactivate
        );
        //addButton(enabledButton); #MCP
        func_230480_a_(enabledButton);

        // if (minecraft == null) { #MCP
        if (field_230706_i_ == null) {
            throw new RuntimeException("Minecraft is null");
        }

        //recipeBookGui.init(width, height, minecraft, false, fContainer); #MCP
        recipeBookGui.init(field_238742_p_, field_238745_s_, field_230706_i_, false, fContainer);
        //children.add(recipeBookGui); #MCP
        field_230705_e_.add(recipeBookGui);
        setFocusedDefault(recipeBookGui);
        recipeButton = new ImageButton(
                rButtonX,
                rButtonY,
                20,
                18,
                0,
                0,
                19,
                RECIPE_BUTTON_TEXTURE,
                this::recipeToggle
        );
        //addButton(recipeButton); #MCP
        func_230480_a_(recipeButton);
        rePositionButtons();
    }

    //public void tick() { #MCP
    public void func_231023_e_() {
        //super.tick(); #MCP
        super.func_231023_e_();
        this.recipeBookGui.tick();
    }

    // public void render(int mouseX, int mouseY, float partialTicks) { #MCP
    public void func_230430_a_(MatrixStack p_230450_1_, int mouseX, int mouseY, float partialTicks) {
        //renderBackground(); #MCP
        func_230446_a_(p_230450_1_);
        // recipeBookGui.render(mouseX, mouseY, partialTicks); #MCP
        recipeBookGui.func_230430_a_(p_230450_1_, mouseX, mouseY, partialTicks);
        //super.render(mouseX, mouseY, partialTicks); #MCP
        super.func_230430_a_(p_230450_1_, mouseX, mouseY, partialTicks);

        boolean ghostItems = false;
        for (int slotIndex = FactoryEntity.resultSlotIndex + 1; slotIndex < FactoryEntity.outputSlotIndex; ++slotIndex) {
            Slot slot = container.inventorySlots.get(slotIndex);
            if (slot.getHasStack()) {
                continue;
            }
            if (slot instanceof ItemSlot) {
                ItemSlot iSlot = (ItemSlot) slot;
                if (!iSlot.enabled) {
                    continue;
                }
                ItemStack fakeStack = new ItemStack(iSlot.lockedItem, 1);
                if (!ghostItems) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.guiLeft, this.guiTop, 0.0F);
                    ghostItems = true;
                }
                RenderSystem.depthFunc(515);
                // itemRenderer.renderItemIntoGUI(fakeStack, slot.xPos, slot.yPos); #MCP
                field_230707_j_.renderItemIntoGUI(fakeStack, slot.xPos, slot.yPos);
                RenderSystem.depthFunc(516);
                int alpha = (int) (0.7 * 0xff);
                int red = 0x8b;
                int blue = 0x8b;
                int green = 0x8b;
                int color = ((alpha * 0x100 + red) * 0x100 + blue) * 0x100 + green;
                // AbstractGui.fill(p_230450_1_, slot.xPos, slot.yPos, slot.xPos + 15, slot.yPos + 15, color); #MCP
                AbstractGui.func_238467_a_(p_230450_1_, slot.xPos, slot.yPos, slot.xPos + 15, slot.yPos + 15, color);
            }
        }
        if (ghostItems) {
            RenderSystem.depthFunc(515);
            RenderSystem.popMatrix();
        }

        // recipeBookGui.renderGhostRecipe(guiLeft, guiTop, true, partialTicks); #MCP
        recipeBookGui.func_230477_a_(p_230450_1_, guiLeft, guiTop, true, partialTicks);
        // renderHoveredToolTip(mouseX, mouseY); #MCP
        func_230459_a_(p_230450_1_, mouseX, mouseY);
        // recipeBookGui.renderTooltip(guiLeft, guiTop, mouseX, mouseY); #MCP
        recipeBookGui.func_238924_c_(p_230450_1_, guiLeft, guiTop, mouseX, mouseY);

        func_212932_b(recipeBookGui);
    }

    @Override
    //protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) { #MCP
    protected void func_230450_a_(MatrixStack p_230450_1_, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //minecraft.getTextureManager().bindTexture(GUI_MAP);
        field_230706_i_.getTextureManager().bindTexture(GUI_MAP);

        //blit(guiLeft, guiTop, 0, 0, 0, xSize, ySize, 256, 256); #MCP
        func_238464_a_(p_230450_1_, guiLeft, guiTop, 0, 0, 0, xSize, ySize, 256, 256);
        renderSlotsBackgrounds(p_230450_1_);
    }

    private void renderSlotsBackgrounds(MatrixStack p_230450_1_) {
        List<Slot> normalSlots = new Stack<>();
        List<Slot> disabledSlots = new Stack<>();
        if (enabled) {
            for (int slotIndex = 0; slotIndex <= FactoryEntity.outputSlotIndex; ++slotIndex) {
                Slot slot = container.inventorySlots.get(slotIndex);
                if (slot instanceof ReceiptSlot) {
                    disabledSlots.add(slot);
                } else if (slot instanceof ItemSlot) {
                    if (((ItemSlot) slot).enabled) {
                        normalSlots.add(slot);
                    } else {
                        disabledSlots.add(slot);
                    }
                }
            }
        } else {
            for (int slotIndex = 0; slotIndex <= FactoryEntity.resultSlotIndex; ++slotIndex) {
                normalSlots.add(container.inventorySlots.get(slotIndex));
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiLeft - 1, guiTop - 1, 0.0F);

        //minecraft.getTextureManager().bindTexture(GUI_MAP); #MCP
        field_230706_i_.getTextureManager().bindTexture(GUI_MAP);
        for (Slot slot : disabledSlots) {
            // slot off: 151, 16, 18, 18
            // blit(p_230450_1_, slot.xPos, slot.yPos, 151, 16, 18, 18); #MCP
            func_238474_b_(p_230450_1_, slot.xPos, slot.yPos, 151, 16, 18, 18);
        }
        for (Slot slot : normalSlots) {
            // slot on: 152, 83, 18, 18
            // blit(p_230450_1_, slot.xPos, slot.yPos, 152, 83, 18, 18); #MCP
            func_238474_b_(p_230450_1_, slot.xPos, slot.yPos, 152, 83, 18, 18);
        }
        RenderSystem.popMatrix();
    }
}
