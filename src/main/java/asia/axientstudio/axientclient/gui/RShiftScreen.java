package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.hud.HudManager;
import asia.axientstudio.axientclient.hud.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class RShiftScreen extends Screen {

    private final Screen parent;
    private final HudManager hud;

    private static final List<String> ELEMENTS = List.of(
        "keystrokes","coords","armor_hub","ping","totem_count","inventory_hub"
    );
    private static final Map<String,int[]> BASE_SIZES = new LinkedHashMap<>();
    static {
        BASE_SIZES.put("keystrokes",    new int[]{66,72});
        BASE_SIZES.put("coords",        new int[]{90,42});
        BASE_SIZES.put("armor_hub",     new int[]{72,28});
        BASE_SIZES.put("ping",          new int[]{80,10});
        BASE_SIZES.put("totem_count",   new int[]{160,10});
        BASE_SIZES.put("inventory_hub", new int[]{162,54});
    }

    private static final int SNAP = 4;
    private String hoveredId   = null;
    private String selectedId  = null;

    // Logo texture
    private static final Identifier LOGO = Identifier.of("axientclient","textures/logo_nobackground.png");

    // Hub button bounds (computed in render)
    private int hubBtnX, hubBtnY, hubBtnW = 160, hubBtnH = 24;

    public RShiftScreen(Screen parent) {
        super(Text.literal("AxientClient"));
        this.parent = parent;
        this.hud = AxientClient.hudManager;
        this.hud.dragMode = true;
    }

    @Override
    protected void init() {
        // No widgets — we draw everything manually + one Open Hub button
        hubBtnX = this.width/2 - hubBtnW/2;
        hubBtnY = this.height/2 + 10; // below logo

        // Preset buttons at bottom
        int bh=18, gap=4, by=this.height-22;
        addDrawableChild(ButtonWidget.builder(Text.literal("Top-Left"),   btn->preset(0)).dimensions(gap,by,78,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Top-Right"),  btn->preset(1)).dimensions(gap+82,by,82,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Competitive"),btn->preset(2)).dimensions(gap+168,by,94,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§cReset Scale"),btn->{ if(selectedId!=null) hud.setScale(selectedId,1f);}).dimensions(gap+266,by,90,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§aSave & Exit"),btn->saveAndExit()).dimensions(this.width-98-gap,by,98,bh).build());
    }

    private void preset(int p) {
        int sw=this.width, sh=this.height;
        switch(p){
            case 0->{ hud.setPos("keystrokes",2,2); hud.setPos("coords",2,80); hud.setPos("armor_hub",2,50); hud.setPos("ping",2,30); hud.setPos("totem_count",2,95); hud.setPos("inventory_hub",2,120); }
            case 1->{ hud.setPos("keystrokes",sw-70,2); hud.setPos("coords",sw-100,2); hud.setPos("armor_hub",sw-76,50); hud.setPos("ping",sw-90,42); hud.setPos("totem_count",sw-170,70); hud.setPos("inventory_hub",sw-168,sh/2); }
            case 2->{ hud.setPos("keystrokes",2,sh-100); hud.setPos("coords",sw/2-45,2); hud.setPos("armor_hub",2,2); hud.setPos("ping",sw-90,2); hud.setPos("totem_count",sw-170,sh-20); hud.setPos("inventory_hub",sw-168,sh/2-40); }
        }
    }

    private void saveAndExit() {
        hud.savePositions();
        hud.dragMode = false;
        AxientClient.config.save();
        this.client.setScreen(parent);
    }

    private int[] scaledSize(String id) {
        int[] b = BASE_SIZES.get(id); float s = hud.getScale(id);
        return new int[]{(int)(b[0]*s),(int)(b[1]*s)};
    }

    private boolean hit(String id, double mx, double my) {
        int[] pos=hud.getPos(id); int[] sz=scaledSize(id); int pad=4;
        return mx>=pos[0]-pad&&mx<=pos[0]+sz[0]+pad&&my>=pos[1]-pad&&my<=pos[1]+sz[1]+pad;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Dim background
        ctx.fill(0, 0, this.width, this.height, 0x55000000);

        // Grid dots
        for (int gx=0;gx<this.width;gx+=SNAP*4)
            for (int gy=0;gy<this.height-28;gy+=SNAP*4)
                ctx.fill(gx,gy,gx+1,gy+1,0x22FFFFFF);

        hoveredId = null;

        // ── Draw HUD element previews ──
        for (String id : ELEMENTS) {
            int[] pos=hud.getPos(id); int[] sz=scaledSize(id);
            boolean drag=id.equals(hud.draggingElement);
            boolean hov=!drag&&hit(id,mouseX,mouseY);
            boolean sel=id.equals(selectedId);
            if(hov) hoveredId=id;

            if(drag||hov||sel){
                int pad=3;
                int bc=drag?0xCC00FF44:sel?0xCCFFAA00:0xCC00CCFF;
                int bg=drag?0x2200FF44:sel?0x22FFAA00:0x2200AAFF;
                ctx.fill(pos[0]-pad,pos[1]-pad,pos[0]+sz[0]+pad,pos[1]+sz[1]+pad,bg);
                ctx.fill(pos[0]-pad,pos[1]-pad,pos[0]+sz[0]+pad,pos[1]-pad+1,bc);
                ctx.fill(pos[0]-pad,pos[1]+sz[1]+pad-1,pos[0]+sz[0]+pad,pos[1]+sz[1]+pad,bc);
                ctx.fill(pos[0]-pad,pos[1]-pad,pos[0]-pad+1,pos[1]+sz[1]+pad,bc);
                ctx.fill(pos[0]+sz[0]+pad-1,pos[1]-pad,pos[0]+sz[0]+pad,pos[1]+sz[1]+pad,bc);
            }

            HudRenderer.renderElement(id, ctx, mc, pos);

            if(hov||sel||drag){
                String badge=String.format("§f%.0f%%",hud.getScale(id)*100);
                int bx=pos[0]+sz[0]+5;
                ctx.fill(bx-2,pos[1]-1,bx+mc.textRenderer.getWidth(badge)+4,pos[1]+9,0xBB000000);
                ctx.drawText(mc.textRenderer,badge,bx,pos[1],0xFFFFAA00,false);
                ctx.drawText(mc.textRenderer,"§7↕scale",bx,pos[1]+10,0x77FFFFFF,false);
            }
        }

        // Compass bar preview
        HudRenderer.renderCompassBar(ctx, mc, mc.player);

        // ── Center panel: Logo + Open Hub button ──
        int logoW=64, logoH=64;
        int logoX=this.width/2-logoW/2;
        int logoY=this.height/2-logoH-4;
        hubBtnY=this.height/2+2;
        hubBtnX=this.width/2-hubBtnW/2;

        // Panel background
        int panelPad=12;
        ctx.fill(hubBtnX-panelPad, logoY-panelPad,
                 hubBtnX+hubBtnW+panelPad, hubBtnY+hubBtnH+panelPad, 0xBB000000);
        ctx.drawBorder(hubBtnX-panelPad, logoY-panelPad,
                hubBtnW+panelPad*2, logoH+hubBtnH+panelPad*2+6, 0x88FFFFFF);

        // Logo (if texture available — fallback to text)
        try {
            ctx.drawTexture(LOGO, logoX, logoY, 0, 0, logoW, logoH, logoW, logoH);
        } catch (Exception ignored) {
            ctx.drawCenteredTextWithShadow(mc.textRenderer,"§b§lAxientClient",this.width/2,logoY+28,0xFFFFFF);
        }

        // Open Hub button
        boolean hubHov = mouseX>=hubBtnX&&mouseX<=hubBtnX+hubBtnW&&mouseY>=hubBtnY&&mouseY<=hubBtnY+hubBtnH;
        ctx.fill(hubBtnX,hubBtnY,hubBtnX+hubBtnW,hubBtnY+hubBtnH, hubHov?0xCC0088CC:0xCC004466);
        ctx.drawBorder(hubBtnX,hubBtnY,hubBtnW,hubBtnH,0xFF00AAFF);
        boolean vi = mc.options.language.startsWith("vi");
        ctx.drawCenteredTextWithShadow(mc.textRenderer,
                vi?"§fMở Menu Mod":"§fOpen Mod Hub",
                this.width/2, hubBtnY+8, 0xFFFFFF);

        // Top hint
        ctx.fill(0,0,this.width,12,0xAA000000);
        ctx.drawCenteredTextWithShadow(mc.textRenderer,
                "§eAxientClient HUD Editor  §7| Drag=move  Scroll=scale  Click=select",
                this.width/2,2,0xFFFFFF);

        // Status
        String status="";
        if(hud.draggingElement!=null){ int[] p=hud.getPos(hud.draggingElement); status="§7Moving §f"+hud.draggingElement+" §7→ "+p[0]+", "+p[1]; }
        else if(selectedId!=null) status="§7Selected: §f"+selectedId+"  §e"+(int)(hud.getScale(selectedId)*100)+"% §7(scroll to resize, R=reset)";
        else if(hoveredId!=null)  status="§7Hover: §f"+hoveredId+" §7— click to select";
        if(!status.isEmpty()) ctx.drawCenteredTextWithShadow(mc.textRenderer,status,this.width/2,this.height-34,0xFFFFFF);

        super.render(ctx,mouseX,mouseY,delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // Hub button
        if(button==0&&mx>=hubBtnX&&mx<=hubBtnX+hubBtnW&&my>=hubBtnY&&my<=hubBtnY+hubBtnH){
            hud.savePositions();
            hud.dragMode=false;
            this.client.setScreen(new ModHubScreen(parent));
            return true;
        }
        if(button==0){
            for(int i=ELEMENTS.size()-1;i>=0;i--){
                String id=ELEMENTS.get(i);
                if(hit(id,mx,my)){
                    selectedId=id;
                    hud.startDrag(id,(int)mx,(int)my);
                    return true;
                }
            }
            selectedId=null;
        }
        return super.mouseClicked(mx,my,button);
    }

    @Override
    public boolean mouseDragged(double mx,double my,int button,double dx,double dy){
        if(button==0&&hud.draggingElement!=null){
            hud.updateDrag((int)mx,(int)my);
            int[] pos=hud.getPos(hud.draggingElement); int[] sz=scaledSize(hud.draggingElement);
            int sx=Math.round(pos[0]/(float)SNAP)*SNAP, sy=Math.round(pos[1]/(float)SNAP)*SNAP;
            hud.setPos(hud.draggingElement,Math.max(0,Math.min(this.width-sz[0],sx)),Math.max(12,Math.min(this.height-sz[1]-28,sy)));
            return true;
        }
        return super.mouseDragged(mx,my,button,dx,dy);
    }

    @Override
    public boolean mouseReleased(double mx,double my,int button){
        if(button==0) hud.stopDrag();
        return super.mouseReleased(mx,my,button);
    }

    @Override
    public boolean mouseScrolled(double mx,double my,double h,double v){
        String t=selectedId!=null?selectedId:hoveredId;
        if(t!=null){ hud.setScale(t,hud.getScale(t)+(v>0?0.1f:-0.1f)); return true; }
        return super.mouseScrolled(mx,my,h,v);
    }

    @Override
    public boolean keyPressed(int kc,int sc,int mod){
        if(kc==GLFW.GLFW_KEY_RIGHT_SHIFT||kc==GLFW.GLFW_KEY_ESCAPE){ saveAndExit(); return true; }
        if(kc==GLFW.GLFW_KEY_R&&selectedId!=null){ hud.setScale(selectedId,1f); return true; }
        String t=hud.draggingElement!=null?hud.draggingElement:selectedId;
        if(t!=null){
            int step=(mod&GLFW.GLFW_MOD_SHIFT)!=0?SNAP*4:SNAP;
            int[] p=hud.getPos(t);
            switch(kc){
                case GLFW.GLFW_KEY_LEFT ->hud.setPos(t,p[0]-step,p[1]);
                case GLFW.GLFW_KEY_RIGHT->hud.setPos(t,p[0]+step,p[1]);
                case GLFW.GLFW_KEY_UP   ->hud.setPos(t,p[0],p[1]-step);
                case GLFW.GLFW_KEY_DOWN ->hud.setPos(t,p[0],p[1]+step);
            }
            return true;
        }
        return super.keyPressed(kc,sc,mod);
    }

    @Override public void close(){ hud.dragMode=false; this.client.setScreen(parent); }
    @Override public boolean shouldPause(){ return false; }
}
