package austeretony.oxygen_exchange.client.gui.settings;

import austeretony.alternateui.screen.framework.GUIElementsFramework;
import austeretony.oxygen_core.client.OxygenManagerClient;
import austeretony.oxygen_core.client.api.ClientReference;
import austeretony.oxygen_core.client.api.EnumBaseGUISetting;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList;
import austeretony.oxygen_core.client.gui.elements.OxygenDropDownList.OxygenDropDownListEntry;
import austeretony.oxygen_core.client.gui.elements.OxygenTextLabel;
import austeretony.oxygen_core.client.gui.settings.ElementsContainer;
import austeretony.oxygen_core.client.gui.settings.gui.ColorButton;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetColorCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetKeyCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetOffsetCallback;
import austeretony.oxygen_core.client.gui.settings.gui.callback.SetScaleCallback;
import austeretony.oxygen_exchange.client.settings.gui.EnumExchangeGUISetting;

public class ExchangeSettingsContainer implements ElementsContainer {

    //interface

    private ColorButton slotBottomLayerColor, slotHighlightingColor;

    private OxygenDropDownList alignmentExchangeMenu;

    private SetColorCallback setColorCallback;

    @Override
    public String getLocalizedName() {
        return ClientReference.localize("oxygen_exchange.gui.settings.module.exchange");
    }

    @Override
    public boolean hasCommonSettings() {
        return false;
    }

    @Override
    public boolean hasGUISettings() {
        return true;
    }

    @Override
    public void addCommon(GUIElementsFramework framework) {}

    @Override
    public void addGUI(GUIElementsFramework framework) {       
        framework.addElement(new OxygenTextLabel(68, 55, ClientReference.localize("oxygen_core.gui.settings.option.color"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //interface background color
        framework.addElement(new OxygenTextLabel(68, 63, ClientReference.localize("oxygen_exchange.gui.settings.option.slotsColor"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));

        framework.addElement(this.slotBottomLayerColor = new ColorButton(68, 65, EnumExchangeGUISetting.SLOT_BOTTOM_LAYER_COLOR.get(), ClientReference.localize("oxygen_exchange.gui.settings.tooltip.bottomLayer")));

        this.slotBottomLayerColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.slotBottomLayerColor.setHovered(false);
            this.setColorCallback.open(this.slotBottomLayerColor);
        });

        framework.addElement(this.slotHighlightingColor = new ColorButton(78, 65, EnumExchangeGUISetting.SLOT_HIGHLIGHTING_COLOR.get(), ClientReference.localize("oxygen_exchange.gui.settings.tooltip.higlighting")));

        this.slotHighlightingColor.setClickListener((mouseX, mouseY, mouseButton)->{
            this.slotHighlightingColor.setHovered(false);
            this.setColorCallback.open(this.slotHighlightingColor);
        });

        framework.addElement(new OxygenTextLabel(68, 25, ClientReference.localize("oxygen_core.gui.settings.option.alignment"), EnumBaseGUISetting.TEXT_SCALE.get().asFloat() - 0.05F, EnumBaseGUISetting.TEXT_ENABLED_COLOR.get().asInt()));

        //exchange menu alignment

        String currAlignmentStr;
        switch (EnumExchangeGUISetting.EXCHANGE_MENU_ALIGNMENT.get().asInt()) {
        case - 1: 
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.left");
            break;
        case 0:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        case 1:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.right");
            break;    
        default:
            currAlignmentStr = ClientReference.localize("oxygen_core.alignment.center");
            break;
        }
        framework.addElement(this.alignmentExchangeMenu = new OxygenDropDownList(68, 35, 55, currAlignmentStr));
        this.alignmentExchangeMenu.addElement(new OxygenDropDownListEntry<Integer>(- 1, ClientReference.localize("oxygen_core.alignment.left")));
        this.alignmentExchangeMenu.addElement(new OxygenDropDownListEntry<Integer>(0, ClientReference.localize("oxygen_core.alignment.center")));
        this.alignmentExchangeMenu.addElement(new OxygenDropDownListEntry<Integer>(1, ClientReference.localize("oxygen_core.alignment.right")));

        this.alignmentExchangeMenu.<OxygenDropDownListEntry<Integer>>setClickListener((element)->{
            EnumExchangeGUISetting.EXCHANGE_MENU_ALIGNMENT.get().setValue(String.valueOf(element.index));
            OxygenManagerClient.instance().getClientSettingManager().changed();
        });

        framework.addElement(new OxygenTextLabel(68, 33, ClientReference.localize("oxygen_exchange.gui.settings.option.alignmentExchangeMenu"), EnumBaseGUISetting.TEXT_SUB_SCALE.get().asFloat() - 0.1F, EnumBaseGUISetting.TEXT_DARK_ENABLED_COLOR.get().asInt()));
    }

    @Override
    public void resetCommon() {}

    @Override
    public void resetGUI() {
        //exchange menu alignment
        this.alignmentExchangeMenu.setDisplayText(ClientReference.localize("oxygen_core.alignment.center"));
        EnumExchangeGUISetting.EXCHANGE_MENU_ALIGNMENT.get().reset();

        //slot color
        EnumExchangeGUISetting.SLOT_BOTTOM_LAYER_COLOR.get().reset();
        this.slotBottomLayerColor.setButtonColor(EnumExchangeGUISetting.SLOT_BOTTOM_LAYER_COLOR.get().asInt());

        EnumExchangeGUISetting.SLOT_HIGHLIGHTING_COLOR.get().reset();
        this.slotHighlightingColor.setButtonColor(EnumExchangeGUISetting.SLOT_HIGHLIGHTING_COLOR.get().asInt());
		
		OxygenManagerClient.instance().getClientSettingManager().changed();
    }

    @Override
    public void initSetColorCallback(SetColorCallback callback) {
        this.setColorCallback = callback;
    }

    @Override
    public void initSetScaleCallback(SetScaleCallback callback) {}

    @Override
    public void initSetOffsetCallback(SetOffsetCallback callback) {}

    @Override
    public void initSetKeyCallback(SetKeyCallback callback) {}
}
