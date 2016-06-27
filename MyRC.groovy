@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.JSON

//import groovyx.net.http.ContentType // this doesn't import ContentType
//import groovyx.net.http.Method // this doesn't import Method
// ContentType static import
import static groovyx.net.http.Method.GET


def dateFormat = "yyyy-MM-dd"
def defaultCurrency = "USD"
def defaultBase = "EUR"
// Method static import
def getExchangeRate = { date, currency, base ->
    def http = new HTTPBuilder('http://api.fixer.io')
    http.request(GET, JSON) { req ->

        if (date) {
            uri.path = "/${date}"
        } else {
            uri.path = '/latest'
        }

        if (base) {
            uri.path +="?base= ${base}"
        }
        response.success = { resp, xml ->
            if (currency) {
                return xml.rates.get(currency);
            }

            return xml.rates
        }
    }
}

def getCurrentExchangeRate = {
    return getExchangeRate(null, it);
}


/*
println "Current Exchange Rates : ${getCurrentExchangeRate("AUD")}"
println "Previous Exchange Rates : ${getExchangeRate("2000-01-03", "AUD")}"

*/

def getDate = {
    def date = ''


    while (!(date =~ /^\d{4}\-(0?[1-9]|1[012])\-(0?[1-9]|[12][0-9]|3[01])\u0024/).matches()) {
        println "Enter Date ${it}: (format YYYY-MM-DD)"
        date = System.in.newReader().readLine()
    }

    return date
}


def getCurrency =  {
    println "Enter Currency (${defaultCurrency})"
    def curr = System.in.newReader().readLine() ?: defaultCurrency
    return curr
}

def getBase =  {
    println "Enter Base (${defaultBase})"
    def base = System.in.newReader().readLine() ?: defaultCurrency
    return base
}
def retrieveRates = { dateFrom, dateTo, currency, base ->
    def rateMap = [:];

    while (dateFrom.before(dateTo)) {
        def  rate = getExchangeRate(dateFrom.format(dateFormat), currency, base)
        rateMap[dateFrom] = rate
        dateFrom = dateFrom.plus(1);

    }

    return rateMap
}



def dateFrom =  Date.parse(dateFormat ,getDate("from") ) ;
def dateTo = Date.parse(dateFormat ,getDate("to") ) ;


println dateFrom
println dateTo

def currency = getCurrency()

def base = getBase()

def rates = retrieveRates(dateFrom, dateTo, currency, base)



