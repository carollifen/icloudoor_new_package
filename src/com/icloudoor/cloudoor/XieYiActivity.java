package com.icloudoor.cloudoor;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class XieYiActivity extends Activity {

	private RelativeLayout back;
	static Point size;
	static float density;
	
	private RelativeLayout content;
	private TextView textview1;
	private TextView tishi;
	private TextView fuwuneirong;
	private TextView zhuceziliao;
	private TextView zhanghaomima;
	private TextView quanli;
	private TextView yiwu;
	private TextView baohu;
	private TextView mianze;
	private TextView zhongzhi;
	private TextView peichang;
	private TextView guanggao;
	private TextView disanfang;
	private TextView shiyong;
	private TextView jieshiquan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xie_yi);
		
		back = (RelativeLayout) findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		//
		Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        density = dm.density;
        display.getSize(size);

        content = (RelativeLayout) findViewById(R.id.content);
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) content.getLayoutParams();
        param.width = dm.widthPixels - 64;
        content.setLayoutParams(param);
        
        textview1 = (TextView) findViewById(R.id.textView1);
        textview1.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��Э�������û������¼��Ϊ������������ݷǹ���Ϣ�Ƽ����޹�˾�����¼��Ϊ�����ǡ���"
        		+ "���������ṩ�Ĳ�Ʒ�ͷ�����������Э�顣"));
        
        tishi = (TextView) findViewById(R.id.tishi);
        tishi.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;������ʼע�����ʹ�����ſ��ŷ���ǰ����Ӧ���߱��л����񹲺͹����ɹ涨��������Ϊ����Ӧ��������Ϊ������"
        		+ "<b>�������߱�ǰ��������Ϊ����Ӧ��������Ϊ���������������ļ໤��Ӧ���շ��ɹ涨�е���˶����µ�һ�к����</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ϊ��������Ȩ�棬��������ע�������е��ͬ�ⱾЭ��֮ǰ��Ӧ�������Ķ���Э�顣"
        		+ "<b>������������Ķ�����������������ݣ��ر�����������������ε�����������ú������������������������ε�����Դ����ʶ����Ӧ�ص��Ķ���</b>"
        		+ "�������ò�ʹ���������ṩ�Ĳ�Ʒ�ͷ�����Ӧ��Ϊ���Ѿ���ϸ�Ķ��˱�Э������ݣ�ͬ�ⱾЭ������ݣ���ͬ�����ر�Э��Ĺ涨��"));
                
        fuwuneirong = (TextView) findViewById(R.id.fuwuneirong);
        fuwuneirong.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.���ſ��ŷ���ľ������������Ǹ���ʵ������ṩ�����磺�Ż���Ϣ�����������ҵ����ȡ�<b>���Ǳ�����ʱ������жϻ���ֹ���ֻ�ȫ�������Ȩ����</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.���ǽ����Ϊ���ṩ��صĿ��ŷ��񣬳���֮������ķ��ã������繺��ѡ������ѵȣ���Ӧ�������и�����"));
        
        zhuceziliao = (TextView) findViewById(R.id.zhuceziliao);
        zhuceziliao.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.���������ſ����˺�ʱ�����������ṩ������ʵ����ϸ��׼ȷ�ĸ������ϣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.���������ṩ�ĸ������Ͻ�������������Ϊʶ�����������û������ݣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.<b>��������ṩ�ĸ���ע����������ʵ���������ѱ����δ��ʱ���£������κ���֮�ӣ����������޷�Ϊ���ṩ��һ���ṩ��Ʒ�ͷ������ǽ����е��κ����Ρ�</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.���������κ�ʱ��ͨ�������ṩ�Ļ�Ա��������ǹ���������;�������»��޸�������ע��ʱ���ṩ�����ϡ�"));
        
        
        zhanghaomima = (TextView) findViewById(R.id.zhanghaomima);
        zhanghaomima.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�˴�ָ������ע��ɹ����˺ż����루�����������룩�����˽��˺���ע��֮�󲻿ɱ�������������ͨ�������ṩ�ķ�������޸ġ�"
        		+ "�����������˺ż�����ı����Լ�ʹ�ø��˺ź����������е�һ���ж�������ȫ�����Ρ��벻Ҫ���˺š�����ת�û���������ʹ�á�"
        		+ "<b>��Ϊ���ı�������������κθ�����Ϊ���������˺Ż����������˷Ƿ�ʹ�ü�������������κκ�������ǲ��е��κ����Ρ�</b>"));
        
        quanli = (TextView) findViewById(R.id.quanli);
        quanli.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.�����Ը��ݱ�Э���Լ����ǲ�ʱ���º͹�������������ʹ�������ṩ�Ĳ�Ʒ�ͷ���<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.����Ȩ��ʹ�������ṩ�Ĳ�Ʒ�ͷ����ڼ�ල���Ǽ����ǵĹ�����Ա�Ƿ��������������ı�׼�����ṩ��Ʒ�ͷ���Ҳ������ʱ��������������ǵĲ�Ʒ�ͷ����йص�����ͽ��顣<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.�������ͬ�ⱾЭ�����������Ǻ������µ����������飬����������ṩ�Ĳ�Ʒ�ͷ������⣬������ѡ��ֹͣʹ�����ǵĲ�Ʒ�ͷ���"
        		+ "<b>�����ѡ��ֹͣʹ�����ǵĲ�Ʒ�ͷ������ǲ��ٶ����е��κ���������Ρ�</b>"));
        
        yiwu = (TextView) findViewById(R.id.yiwu);
        yiwu.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.��ͬ�ⰴ�����ǲ�ʱ������������޸ĵı�Э���������������ܲ�ʹ�����ǵĲ�Ʒ�ͷ���������ͨ�����������ֶλ���������ƽ���ֶ�ʹ�����ǵĲ�Ʒ�ͷ����������ǻ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.�����ø��������������ṩ��Ʒ�ͷ��񣬰����������ڣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��1���������������ǵ���վ��������<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��2���ƽ⡢�޸������ṩ�Ŀͻ��˳���<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��3���������������ǵķ�������������˳���<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��4��������ʹ�á������������κ���ʽ�ķ������ǲ�Ʒ�ĸ������ߣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��5�����ó����©���ʹ���Bug���ƻ����ǲ�Ʒ���������У�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��6��������ظ��Ż��谭����ʹ���������ṩ�Ĳ�Ʒ�ͷ���<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.<b>�����뱣�ܺ��Լ����˺ź����룬��������ԭ�����˺ź�����й�ܶ���ɵĺ�������������ге���</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.��������һ�������ĸ��˵����ʹ���������ṩ�Ĳ�Ʒ�ͷ��������������������ṩ�Ĳ�Ʒ�ͷ��������ҵĿ�ĵĻ��Ҳ�����������ǵĲ�Ʒ�ͷ���������ۻ�������ҵĿ�ĵĻ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5.��Ӧ���Լ��˺��е����л���¼�������Ӧ�����йػ�������Ϣ�������йط��ɡ����漰ͨ�����õĻ�����һ����º����ǵĹ淶���������ге�������������Ϣ���������������Ρ��ر�ǿ���������÷����������ݣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��1�������ܷ���ȷ���Ļ���ԭ��ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��2��Σ�����Ұ�ȫ��й¶�������ܣ��߸�������Ȩ���ƻ�����ͳһ�ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��3���𺦹�������������ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��4��ɿ�������ޡ��������ӣ��ƻ������Ž�ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��5���ƻ������ڽ����ߣ�����а�̺ͷ⽨���ŵģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��6��ɢ��ҥ�ԣ�������������ƻ�����ȶ��ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��7��ɢ�����ࡢɫ�顢�Ĳ�����������ɱ���ֲ����߽�������ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��8��������߷̰����ˣ��ֺ����˺Ϸ�Ȩ��ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��9�����з��ɡ����������ֹ���������ݵģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��10���ַ��κε����ߵ�֪ʶ��Ȩ����Ȩ����/˽��Ȩ���ģ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��11��Υ�����ĵ��¡�����ϰ�ߵġ�"));
        
        baohu = (TextView) findViewById(R.id.baohu);
        baohu.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���Ƿǳ�������������Ϣ����˽�ı���������ʹ�������ṩ�ķ���ʱ����ͬ�����ǰ��ձ�Э���Լ���ռ������桢ʹ�á���¶�ͱ������ĸ�����Ϣ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���Ƕ����ĸ�����Ϣ�е��������񣬲���Ϊ�����������Ӫ��Ŀ�Ķ�������ۻ���������κ���Ϣ�����ǻ�����������²Ž����ĸ�����Ϣ�����������<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.���Ȼ������ͬ�����Ȩ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.���ݷ��ɷ���Ĺ涨��������˾��������Ҫ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.�����ǵĹ������������ĸ�����Ϣ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4.��������ĺ�������ṩ���ĸ�����Ϣ�������Ǹ������ǵ�ָʾ����ѭ���ǵ���˽Ȩ�����Լ������κ���Ӧ�ı��ܺͰ�ȫ��ʩ��Ϊ���Ǵ�����Щ��Ϣ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5.Ϊ�������ǵ�֪ʶ��Ȩ�������Ʋ�Ȩ�棬��Ҫ��¶���ĸ������ϡ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.ֻ�й���������Ϣ�������ṩ����Ҫ�ķ��񣬻����������˵ľ��׻����顣<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.��������Υ���й��йط��ɡ�������߱�Э����������Ҫ���������¶��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8.Ϊά�������û��ĺϷ�Ȩ�档<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���ǽ���ȡ��ҵ�Ϻ���ķ�ʽ�Ա������ĸ�����Ϣ�İ�ȫ�����ǽ�ʹ��ͨ�����Ի�õİ�ȫ�����ͳ������������ĸ������ϲ���δ����Ȩ�ķ��ʡ�ʹ�û�й©��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>�ڲ�͸¶�����û���˽���ϵ�ǰ���£�������Ȩ�������û����ݿ���м������������ѽ��з������������û����ݿ������ҵ�ϵ����á�</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>���ܶ��û�����˽Ȩ�������˼����Ŭ����������Ȼ���ܱ�֤���еİ�ȫ������ʩʹ�û��ļ�����Ϣ�Ȳ����κ���ʽ����ʧ��</b>"));
        
        mianze = (TextView) findViewById(R.id.mianze);
        mianze.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>1.���ǲ��������ṩ�ķ�������κ���ʽ�ı�֤���������������������ˣ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��1�������񽫷�����������<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��2�������񽫲��ܸ��š���ʱ�ṩ����ȫ�ɿ��򲻻����<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.���Ѿ��˽Ⲣͬ����������ǡ����ǵĺ�����������ϵͳ��Ӳ���豸�Ĺ��ϡ�ʧ�����Ϊ��������ʧ��ȫ���򲿷��жϡ���ʱ�޷�ʹ�á����Ӳ���������ṩ��Ʒ�ͷ����ֹͣ���жϵģ����ǲ��е��κ����Ρ������������������ǵ������ϵͳ���۸ġ�ɾ�Ļ�α�졢������վ���û����ϻ����ݶ�������ǵĲ�Ʒ�ͷ����ֹͣ���жϣ�����Ҳ���е��κ����Ρ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.���������ɹ涨�е������������񣬵��޷���������Ϣ�����豸ά�������ӹ��ϣ����ԡ�ͨѶ������ϵͳ�Ĺ��ϣ��������ϣ��չ������ң����֣���ˮ���籩����ը��ս����������Ϊ��˾���������ص�������������ԭ���������ɵ��𺦽���е����Ρ�</b>"));
        
        zhongzhi = (TextView) findViewById(R.id.zhongzhi);
        zhongzhi.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>1.����ϵͳά������������Ҫ������ͣ����������ǽ����������Ƚ���ͨ�档�ڴ������£����ǲ��е��κ����Ρ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.��Ӧȷʵ���ر�Э�鼰�йط��ɷ���Ĺ涨�����Ƕ������Ƿ�Υ����Э��ӵ�������϶�Ȩ���緢�������κ�һ�����Σ�������Ȩ��ʱ�жϻ���ֹ�����ṩ��Э�����µ�������������֪ͨ��ͬʱ������ͣ����ֹ��ɾ�����˺������˺��е�����������ϡ��������κμ�¼���Լ�ȡ����ֹͣ���������Ļ�Ա�ʸ�<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��1�����ṩ�ĸ������ϲ���ʵ��<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��2����Υ�����ɷ����Э����κ�Լ����<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3.��ǰ�����������⣬���Ǳ����ڲ�����֪ͨ�����������ʱ�жϻ���ֹ���ֻ�ȫ����������Ȩ�����������з�����жϻ���ֹ����ɵ��κ���ʧ������������κε������е��κ����Ρ�</b>"));
        
        peichang = (TextView) findViewById(R.id.peichang);
        peichang.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;����Υ����Э�����ط��ɷ��棬�������ǵĹ�����ҵ���ܹ��ˡ������˼�����һ��������и�����Ա����ܵ��𺦻�֧��һ���������ã�������������֧��������ʿ�������Υ����Ϊ�����е�һ�б绤���������ϼ���غͽ�֮���ɷ��ã�����Ӧ�е���Ӧ��ΥԼ���μ����⳥���Ρ�"));
        
        guanggao = (TextView) findViewById(R.id.guanggao);
        guanggao.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���ǿ��ܿ�����ҵ��������������Ĺ�档��Щ����ϵ����̻���Ʒ�����ṩ����Ϊ�����ǽ��ṩ�������ݵ�ý�顣��ͨ�����ǻ����������ӵ���վ������ķ������Ʒ���佻����Ϊ�������������Ʒ�������ṩ��֮�䣬�������޹ء�"));
        
        disanfang = (TextView) findViewById(R.id.disanfang);
        disanfang.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��������ʹ�����ǵĲ�Ʒ�ͷ�����������ӵ���������վ�㡣��������վ�㲻�����ǿ��ƣ���������Ҳ�����κε�����վ������ݡ�������վ��������κ����ӡ��������վ����κθ��Ļ���¸���"
        		+ "<b>���ǽ�Ϊ���ṩ������Ŀ�Ķ������ṩ��Щ��������վ������ӣ��������ṩ����Щ���Ӳ�����ζ�������Ͽɸõ�����վ�㡣����Ҫ��鲢���ظõ�����վ�����ع涨��</b>"));
        
        shiyong = (TextView) findViewById(R.id.shiyong);
        shiyong.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;��Э��֮��������Ч�����͡��޶������䡢��ֹ��ִ�����������������л����񹲺͹���½�������ɣ��編������ع涨�ģ�������ҵ������/����ҵ������<br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;����ʹ�����ǵĲ�Ʒ�ͷ���������κ����飬��������Э�̽����Э�̲��ɵģ���һ����Ȩ���ҹ�˾���ڵ�����Ժ���ߡ�"));
        
        jieshiquan = (TextView) findViewById(R.id.jieshiquan);
        jieshiquan.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���ڶ����Ǳ����û����г�״�����ϱ仯�Ŀ��ǣ����Ǳ�����ʱ�޸ġ�������ɾ����Э�������Ȩ�����޸ġ�������ɾ����Э������ʱ�����ǽ��ڹٷ���վ��ҳ�����޸ġ�������ɾ������ʵ���������ж������и���֪ͨ��"
        		+ "<b>������ͬ���������޸ġ�������ɾ�������ݣ���ֹͣʹ���������ṩ�ķ�����������ʹ�����ǵķ�������ͬ��ͬ�Ⲣ���ܱ�Э�������޸ġ�������ɾ����֮һ�����ݣ��Ҳ�����˶�Ҫ���κβ������⳥��</b><br>"
        		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���Ǳ����Ա�Э�������Ʒ�ͷ����Լ��������ṩ�Ĳ�Ʒ�ͷ������عٷ���վ�����ս���Ȩ��"));
        
        new Thread(){
        	
        	@Override
			public void run() {
        		TextJustification.justify(textview1, size.x - 64);
        		TextJustification.justify(tishi, size.x - 64);
        		TextJustification.justify(fuwuneirong, size.x - 64);
        		TextJustification.justify(zhuceziliao, size.x - 64);
        		TextJustification.justify(zhanghaomima, size.x - 64);
        		TextJustification.justify(quanli, size.x - 64);
        		TextJustification.justify(yiwu, size.x - 64);
        		TextJustification.justify(baohu, size.x - 64);
        		TextJustification.justify(mianze, size.x - 64);
        		TextJustification.justify(zhongzhi, size.x - 64);
        		TextJustification.justify(peichang, size.x - 64);
        		TextJustification.justify(guanggao, size.x - 64);
        		TextJustification.justify(disanfang, size.x - 64);
        		TextJustification.justify(shiyong, size.x - 64);
        		TextJustification.justify(jieshiquan, size.x - 64);
        	}
        }.start();
        
	}

}
