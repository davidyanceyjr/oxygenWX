"""Reproduce TP.1D static references: python .../renders/generate.py (ImageMagick + librsvg)."""
from pathlib import Path
import json, math, subprocess, functools, html
ROOT=Path(__file__).resolve().parents[4]
OUT=Path(__file__).resolve().parent
E=ROOT/'.codex/test-artifacts/028-tp-1d-integrated-pack-review'
E.mkdir(parents=True,exist_ok=True)
F=json.loads((OUT/'fixture.json').read_text())
THEMES=['atmospheric','glass','minimal_oled','instrument','terminal']
NAMES=['Atmospheric','Glass','Minimal OLED','Instrument','Terminal']
@functools.lru_cache(None)
def fontfile(family):
 return subprocess.check_output(['fc-match','-f','%{file}',family],text=True)
@functools.lru_cache(None)
def measure(s,size,family):
 return float(subprocess.check_output(['magick','-font',fontfile(family),'-pointsize',str(size),'label:'+s,'-format','%w','info:'],text=True))
def lines(s,size,width,family):
 result=[]; line=''
 for word in s.split():
  test=(line+' '+word).strip()
  if line and measure(test,size,family)>width: result.append(line); line=word
  else: line=test
 if line:result.append(line)
 assert all(measure(t,size,family)<=width+1 for t in result),(s,width)
 return result

def render(theme,page,condition='primary',width=393,height=852,scale=1,rtl=False,off=False,hc=False):
 t=json.loads((ROOT/f'docs/theme-system/tokens/catalog/{theme}.json').read_text())
 c=t['colors']; sp=t['spacingDp']; sf=t['surface']; off=off or theme in ['minimal_oled','terminal'];
 family='Fira Sans' if theme=='atmospheric' else ('Noto Sans Mono' if theme=='terminal' else 'Noto Sans')
 g=sp['gutter']; gap=sp['grid']; stack=sp['stack']; pad=sp['panel']; w=min(width-2*g,480); x=(width-w)/2
 primary=c['content']; secondary=primary if hc else c['secondaryData']; surf=c['surface']; outline=primary if hc else c['outline']
 parts=[]; boxes=[]
 def rect(x,y,w,h,fill,rx=0,stroke='none',opacity=1):
  parts.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" fill="{fill}" fill-opacity="{opacity}" stroke="{stroke}"/>')
 def text(s,x,y,w,size=16,lh=24,color=None,weight=400,field=None):
  sz=size*scale; l=lh*scale; ls=lines(s,sz,w,family)
  attr=f' data-field="{field}"' if field else ''
  # English fixture strings retain LTR bidi within the mirrored layout.
  xx=x+w if rtl else x
  for j,line in enumerate(ls):
   parts.append(f'<text x="{xx}" y="{y+j*l+sz}" font-size="{sz}" font-weight="{weight}" fill="{color or primary}" text-anchor="{"end" if rtl else "start"}"{attr}>{html.escape(line)}</text>')
   boxes.append(dict(text=line,x=x,y=y+j*l,width=measure(line,sz,family),available=w,height=l,field=field))
  return len(ls)*l
 def panel(x,y,w,h,hero=False):
  if theme in ['minimal_oled','terminal']:
   parts.append(f'<path d="M{x} {y+h}h{w}" stroke="{outline}"/>');return
  rect(x,y,w,h,surf,24 if hero and theme=='glass' else sf['radiusDp'],outline if sf['borderDp'] else 'none',1 if off or hc else sf['opacity'])
 def button(label,x,y,w,disabled=False):
  ls=lines(label,14*scale,w-16,family); h=max(48,len(ls)*20*scale+16)
  rect(x,y,w,h,c['elevatedSurface'] if not disabled else c['canvas'],min(sf['radiusDp'],12),outline)
  text(label,x+8,y+8,w-16,14,20,secondary if disabled else primary)
  return h
 def mark(x,y,partly=False):
  parts.append(f'<g aria-hidden="true" fill="none" stroke="{c["conditionAccent"]}" stroke-width="1.6">')
  parts.append(f'<circle cx="{x+18}" cy="{y+18}" r="8"/>')
  for a in range(0,360,45):
   rad=math.radians(a);parts.append(f'<path d="M{x+18+12*math.cos(rad):.2f} {y+18+12*math.sin(rad):.2f}l{4*math.cos(rad):.2f} {4*math.sin(rad):.2f}"/>')
  if partly:parts.append(f'<path fill="{c["canvas"]}" d="M{x+8} {y+33}c-9 0-9-13 0-13c0-14 21-14 21 0c13-2 15 13 3 13Z"/>')
  parts.append('</g>')
 rect(0,0,width,height,c['canvas'])
 if not off and not hc:
  parts.append(f'<defs><linearGradient id="sky" x2="0" y2="1"><stop stop-color="{c["atmosphereTop"]}"/><stop offset="1" stop-color="{c["atmosphereBottom"]}"/></linearGradient><radialGradient id="glow"><stop stop-color="{c["atmosphereGlow"]}" stop-opacity=".18"/><stop offset="1" stop-color="{c["atmosphereGlow"]}" stop-opacity="0"/></radialGradient></defs>')
  rect(0,0,width,height,'url(#sky)');parts.append(f'<ellipse cx="{width*.85}" cy="260" rx="280" ry="330" fill="url(#glow)"/>')
  if theme=='instrument':
   for xx in range(0,width,32):parts.append(f'<path d="M{xx} 24V{height-24}" stroke="{c["outline"]}" stroke-opacity=".18"/>')
 # Chosen reference insets 24 top and 24 bottom; never a runtime inset constant.
 y=24
 text(F['current']['location'],x,y,w,20,28,weight=600,field='current.location')
 text(page,x,y+30,w,14,20,secondary)
 y+=56+stack
 tabs=['Now','Hourly','Daily','Details']; tw=w/4; tabh=max(48,20*scale+16)
 for i,name in enumerate(tabs):
  xx=x+(3-i if rtl else i)*tw
  if name==page:rect(xx,y,tw,tabh,c['elevatedSurface'],min(sf['radiusDp'],12),outline)
  text('['+name+']' if theme=='terminal' and name==page else name,xx+6,y+8,tw-12,14,20,weight=600 if name==page else 400)
  if name==page:rect(xx+8,y+tabh-4,tw-16,2,primary)
 y+=tabh+stack; bodytop=y; body_start=len(parts)
 if page=='Now':
  hero_w=min(w,325 if theme=='glass' else 338 if theme=='instrument' else w); hx=(width-hero_w)/2
  hp={'atmospheric':32,'glass':28,'minimal_oled':40,'instrument':10,'terminal':32}[theme]
  hy=y; panel_index=len(parts);parts.append('')
  y+=24
  text(F['current']['temperature'],hx+hp,y,hero_w-2*hp,48 if theme=='terminal' else 56,56 if theme=='terminal' else 64,weight=500,field='current.temperature')
  if scale==1:mark(hx+(hp if rtl else hero_w-hp-40),y+8,True)
  y+=64*scale
  y+=text(F['current']['condition'],hx+hp,y,hero_w-2*hp,20 if theme=='atmospheric' else 18,28 if theme=='atmospheric' else 24,field='current.condition')
  y+=8
  y+=text('Feels '+F['current']['apparent'],hx+hp,y,hero_w-2*hp,16,24,secondary,field='current.apparent')
  y=max(hy+220,y+24)
  if theme in ['glass','instrument']:
   n=len(parts);panel(hx,hy,hero_w,y-hy,True);parts[panel_index]=parts.pop()
  y+=stack
  slots=[('Humidity','humidity',None),('Dew point','dewPoint',None),('Precipitation','precipitationHeadline','precipitationSupporting'),('Wind','windHeadline','windSupporting')]
  cols=1 if scale>1 or (w-gap)/2<144 else 2; cw=(w-gap*(cols-1))/cols
  for start in range(0,4,cols):
   row=slots[start:start+cols]; heights=[]
   for label,key,support in row:
    heights.append(2*pad+20*scale+8+len(lines(F['current'][key],16*scale,cw-2*pad,family))*24*scale+(len(lines(F['current'][support],12*scale,cw-2*pad,family))*18*scale+4 if support else 0))
   rh=max(heights)
   for j,(label,key,support) in enumerate(row):
    xx=x+(cols-1-j if rtl else j)*(cw+gap);panel(xx,y,cw,rh)
    yy=y+pad;yy+=text(label,xx+pad,yy,cw-2*pad,14,20,secondary);yy+=8
    yy+=text(F['current'][key],xx+pad,yy,cw-2*pad,16,24,field='current.'+key)
    if support:text(F['current'][support],xx+pad,yy+4,cw-2*pad,12,18,secondary,field='current.'+support)
   y+=rh+gap
  y+=stack-gap
 else:
  range_w=min(w,325 if theme=='glass' else 337 if theme=='instrument' else w);rx=(width-range_w)/2
  y+=text(F['window']['rangeLabel'],rx,y,range_w,20,28,field='window.rangeLabel')+8
  y+=button('Choose forecast date',rx,y,range_w)+stack
  cols=1 if scale>1 or (w-gap)/2<144 else 2;cw=(w-gap*(cols-1))/cols
  for start in range(0,6,cols):
   rh=max(112,2*pad+(20+24+28)*scale+8)
   for j,entry in enumerate(F['window']['entries'][start:start+cols]):
    i=start+j;xx=x+(cols-1-j if rtl else j)*(cw+gap); panel(xx,y,cw,rh)
    yy=y+pad
    yy+=text(entry['time'],xx+pad,yy,cw-2*pad,12 if theme=='glass' else 14,20,secondary,field=f'entries.{i}.time')
    yy+=text(entry['condition'],xx+pad,yy,cw-2*pad,14 if theme=='terminal' else 16,24,field=f'entries.{i}.condition')
    yy+=text(entry['temperature'],xx+pad,yy,cw-2*pad,18 if theme=='terminal' else 20,28,field=f'entries.{i}.temperature')
    if scale==1:mark(xx+(pad if rtl else cw-pad-36),y+rh-42)
    assert entry['precipitation'] is None
   y+=rh+gap
  y+=stack-gap
  # Controls in document order; compact fixed footer is separately specified below.
  cw=(w-gap)/2
  bh=max(button('Earlier · disabled',x+(cw+gap if rtl else 0),y,cw,True),button('Later',x+(0 if rtl else cw+gap),y,cw))
  y+=bh+stack
 # Exact source strings with natural wrapping. Status is illustrative mapped input, not a live claim.
 sh=2*pad+sum(len(lines(F[key],12*scale,w-2*pad,family))*18*scale+6 for key in ['sourceLine','updatedLine','status'])
 if theme not in ['minimal_oled','terminal']:rect(x,y,w,sh,c['elevatedSurface'],sf['radiusDp'],outline if hc else 'none')
 else:panel(x,y,w,sh)
 yy=y+pad
 for key in ['sourceLine','updatedLine','status']:yy+=text(F[key],x+pad,yy,w-2*pad,12,18,secondary,field=key)+6
 y+=sh+stack
 fullheight=max(height,math.ceil(y+24)); body=parts[body_start:];shell=parts[:body_start]
 # At short heights, keeping both body and footer leaves < two rows: document-order fallback.
 # Each SVG captures scroll offset zero. Companion end/full captures prove reachable content.
 overflow=max(0,y-(height-24)); avail=height-24-bodytop
 name=f'{theme}-{page.lower()}'+('' if condition=='primary' else '-'+condition)
 meta=dict(theme=theme,page=page,condition=condition,viewport=[width,height],fontScale=scale,rtl=rtl,effects='Off' if off else 'Subtle',contrast='High' if hc else 'Standard',insets=[24,0,24,0],bodyTop=bodytop,bodyHeight=y-bodytop,scrollMax=overflow,font=family,fixture='fixture.json',scrollOffset=0)
 def svg(content,h):
  return f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{h}" viewBox="0 0 {width} {h}" font-family="{family}"><title>{NAMES[THEMES.index(theme)]} / {page} / {condition} — illustrative design reference</title><metadata>{html.escape(json.dumps(meta))}</metadata>'+''.join(content)+'</svg>\n'
 clip=f'<defs><clipPath id="body"><rect x="0" y="{bodytop}" width="{width}" height="{avail}"/></clipPath></defs>'
 (OUT/(name+'.svg')).write_text(svg(shell+[clip,'<g clip-path="url(#body)">']+body+['</g>'],height))
 # Entire logical scroll body plus shell, for review only (not an additional viewport target).
 full=[f'<rect width="{width}" height="{fullheight}" fill="{c["canvas"]}"/>']+parts
 (E/(name+'-full.svg')).write_text(svg(full,fullheight))
 (E/(name+'-end.svg')).write_text(svg(shell+[clip,f'<g clip-path="url(#body)"><g transform="translate(0 {-overflow})">']+body+['</g></g>'],height))
 (E/(name+'-bounds.json')).write_text(json.dumps(boxes,indent=2))
 return dict(file=name+'.svg',**meta)

def main():
 records=[]
 for theme in THEMES:
  for page in ['Now','Hourly']:records.append(render(theme,page))
 records.extend([render('glass','Now','compact',360,640),render('glass','Hourly','font-1.3',scale=1.3),render('terminal','Hourly','rtl',rtl=True),render('atmospheric','Now','wide',840,900),render('glass','Now','effects-off',off=True),render('instrument','Hourly','high-contrast',hc=True)])
 (OUT/'index.json').write_text(json.dumps(records,indent=2)+'\n')
 for r in records:
  name=Path(r['file']).stem
  for src,dest in [(OUT/r['file'],E/(name+'.png')),(E/(name+'-full.svg'),E/(name+'-full.png')),(E/(name+'-end.svg'),E/(name+'-end.png'))]:
   subprocess.run(['rsvg-convert',str(src),'-o',str(dest)],check=True)
 print('Generated 10 primary references, 6 examples, and full/end review captures.')
if __name__=='__main__':main()
