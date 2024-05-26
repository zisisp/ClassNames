//+------------------------------------------------------------------+
//|                                                                  |
//|                               Copyright 2017, www.mql4trader.com |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright 2017, www.mql4trader.com"

//--- input parameters
extern string    Autotraders_by ="www.mql4trader.com";

///CUSTOMIZABLE OPTIONS GO HERE///
extern int period = 300; // PERIOD
extern int applied_price = 0;  // APPLIED PRICE (0=CLOSE,1=OPEN,2=HIGH,3=LOW,4=MEDIAN,5=TYPICAL,6=WEIGHTED          
extern int barshift  = 1;           // SHIFT (1=last bar, 2= 2 bars ago, 3= 3 bars ago ect
////

///Set Take Profit and Stoploss///(Make sure that you check the journal when strategy testing. 
//If you are getting a order modify error your stops may be to low. 
//Check the specifications of the symbol you are trading and adjust for the amount of digits used.
extern double   TakeProfit=120;
extern double   StopLoss=150;
extern double   Lots=1; ///Investment 

//Timesettings
extern string   TimeSettings="Set the hours the EA should trade";
extern int      StartHour=0;//24 hour clock. Set your start and end of day times or leave as is to trade every hour
extern int      EndHour=23;

//Slippage
extern int      Slip=10;///adjust slippage here

///Leave BarsCount as is/// This makes it so you can only place one trade per bar. 
//Otherwise it sometimems will fire off many of the same trade per bar as each tick occurs...
extern int     BarsCount=0;///leave this as is


//Lets Begin//If there are less than zero orders, we can trade//

int start(){


if (Bars>BarsCount) 


{
//////////////////
string Symb=Symbol();

////This advisor works on all charts because of the "MarketInfo" code below...

int digits=MarketInfo(Symbol(),MODE_DIGITS);
int StopMultd=10;
int Slippage=Slip*StopMultd;

///Magic Number is your orders unique id number. This way you do not open multiple orders///

int MagicNumber1=032017,MagicNumber2=042017,i,closesell=0,closebuy=0;


///Takeprofit and Stoploss settings

double  TP=NormalizeDouble(TakeProfit*StopMultd,Digits);
double  SL=NormalizeDouble(StopLoss*StopMultd,Digits);

double slb=NormalizeDouble(Ask-SL*Point,Digits);
double sls=NormalizeDouble(Bid+SL*Point,Digits);


double tpb=NormalizeDouble(Ask+TP*Point,Digits);
double tps=NormalizeDouble(Bid-TP*Point,Digits);



//-------------------------------------------------------------------+
//Check open orders
//-------------------------------------------------------------------+



if(OrdersTotal()>0){
  for(i=1; i<=OrdersTotal(); i++)          // Checking to make sure there are no open orders. Keep this set to zero unless you want the advisor to open more than one buy at a time or more than one sell at a time.
     {
      if (OrderSelect(i-1,SELECT_BY_POS)==true) // If the next is available
        {
          if(OrderMagicNumber()==MagicNumber1) {int halt1=1;}///if this magicnumber has an order open... halt!
          if(OrderMagicNumber()==MagicNumber2) {int halt2=1;}///if this magicnumber has an order open... halt!









        }
     }
}
////Change input parameters "StartHour" EndHour" at top of page, to adjust, if you only want to trade at certain times...

if((Hour()>=StartHour)&&(Hour()<EndHour))
{
int TradeTimeOk=1;
}
else
{ TradeTimeOk=0; }


////



//-----------------------------------------------------------------
// Indicators Section/// This is where you place indicators needed for expert advisor to work...
//-----------------------------------------------------------------

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                              //
// IF YOU DO NOT UNDERSTAND THIS INDICATOR, SIMPLY RUN IT THROUGH THE STRATEGY TESTER AS IS AND "OPEN CHART"... //
// NOW YOU CAN GET A BETTER IDEA OF HOW IT WORKS...                                                             //
// IT IS ALSO RECOMMENDED TO SEARCH ONLINE TO LEARN MORE ON HOW TO USE THIS INDICATOR                           //
//                                                                                                              //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////Always type "double" then the "name" you want to give, then "=" , then your indicator code...
///NOTE: The strategy tester can only give results at the end of each bar on a chart.
///This is why we use "1" as our shift instead of "0".
///If we use 1 then the advisor makes its calculation immediatly after the last bar has closed.
///You can still use "0" , but the strategy tester will not pick up on it...


double RSI1=iRSI(NULL,0,14,0,1);

///NOTE: TO MAKE LIFE EASIER, USE THE INPUT PARAMETERS FROM TOP OF PAGE.
///THIS ALLOWS YOU TO ADJUST SETTINGS FROM THE STRATEGY TESTER
///HERE IS THE SAME INDICATOR THAT IS NOW CUSTOMIZABLE THROUGH THE STRATEGY TESTER:

double RSI1CUSTOM=iRSI(NULL,0,period,applied_price,barshift);

//leave the first two "NULL" and "0". Null means it will trade any chart its on and 0 means it will use the chart timeframe that it is attached to.



 //-------------------------------------------------------------------
// Opening criteria for buy and sell orders
//-----------------------------------------------------------------------------------------------------

Comment("TEMPLATE INDICATOR AUTOTRADER");

//OPEN SELL//

//USE "&&" TO ADD MORE CONDITIONS IN BRACKETS...

if((RSI1CUSTOM>70)&&(halt1!=1)&&(TradeTimeOk==1)){
int opensell=OrderSend(Symbol(),OP_SELL,Lots,Bid,Slip,0,0,"TEMPLATE",MagicNumber1,0,Green);
 }



//OPEN BUY//

//USE "&&" TO ADD MORE CONDITIONS IN BRACKETS...

 if((RSI1CUSTOM<30)&&(halt2!=1)&&(TradeTimeOk==1)){
int openbuy=OrderSend(Symbol(),OP_BUY,Lots,Ask,Slip,0,0,"TEMPLATE",MagicNumber2,0,Blue);

 }


 //-------------------------------------------------------------------------------------------------
// Closing criteria for buy and sell orders
//-------------------------------------------------------------------------------------------------




if(openbuy>=0||opensell>=0)


{// start

if(OrdersTotal()>=0){
  for(i=1; i<=OrdersTotal(); i++){          // Cycle searching in orders

      if (OrderSelect(i-1,SELECT_BY_POS)==true){ // If the next is available...

//SELL CLOSE//
if((OrderMagicNumber()==MagicNumber1)&&(RSI1<30)){
int sellclose=OrderClose(OrderTicket(),Lots,Ask,Slip,Red);
}

//BUY CLOSE//
if((OrderMagicNumber()==MagicNumber2)&&(RSI1>70)){
int buyclose=OrderClose(OrderTicket(),Lots,Bid,Slip,Red);

}


////take profit and stoploss code///Do not change this code.
//Just change takeprofit and stoploss settings in "Input Parameters"
//If you do not need takeprofit or stoploss you can delete these 4 lines of code...

 if((OrderMagicNumber()==MagicNumber2)&&(OrderTakeProfit()==0)&&(OrderSymbol()==Symbol())){ int modify1=OrderModify(OrderTicket(),0,OrderStopLoss(),tpb,0,CLR_NONE); }
 if((OrderMagicNumber()==MagicNumber1)&&(OrderTakeProfit()==0)&&(OrderSymbol()==Symbol())){ int modify2=OrderModify(OrderTicket(),0,OrderStopLoss(),tps,0,CLR_NONE); }
 if((OrderMagicNumber()==MagicNumber2)&&(OrderStopLoss()==0)&&(OrderSymbol()==Symbol())){ int modify3=OrderModify(OrderTicket(),0,slb,OrderTakeProfit(),0,CLR_NONE); }
 if((OrderMagicNumber()==MagicNumber1)&&(OrderStopLoss()==0)&&(OrderSymbol()==Symbol())){ int modify4=OrderModify(OrderTicket(),0,sls,OrderTakeProfit(),0,CLR_NONE); }

        }
     }

}
}
}// stop /// Do Not Alter The Following Code ///



 //----
int Error=GetLastError();
  if(Error==130){Alert("Wrong stops. Retrying."); RefreshRates();}
  if(Error==133){Alert("Trading prohibited.");}
  if(Error==2){Alert("Common error.");}
  if(Error==146){Alert("Trading subsystem is busy. Retrying."); Sleep(500); RefreshRates();}

//----

//-------------------------------------------------------------------
BarsCount=Bars;
return(0);

}
 

//+------------------------------------------------------------------+