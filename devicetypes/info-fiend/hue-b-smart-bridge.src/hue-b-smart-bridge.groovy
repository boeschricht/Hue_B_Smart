/**
 *  Hue B Smart Bridge
 *
 *  Copyright 2017 Anthony Pastor
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Version 1.0
 *	Version 1.1 - added discoverBulbs, discoverGroups, discoverScenes, discoverSchedules, pollItems, pollBulbs, pollGroups, pollScenes, pollSchedules.
 * 				- Changed device to multiAttribute tile; added bridge & health check capability; added status attribute; added device-enroll
 *              - Receipt of successful change to a Group will now cause ST to immediately update status of all bulbs in that group!!!
 *  Version 1.2 - Fixed problem of devices not loading in HBS smartapp; removed scenes and groups created by new Hue app schedules.
 *	Version 1.3 - Added TMleafs edits
 *
 *  Version 2.0 - added Hue Hub Rules (needs new Hue B Smart Rules DTH); fixed handling of success responses Hue Hub
 */

metadata {
	definition (name: "Hue B Smart Bridge", namespace: "info_fiend", author: "Anthony Pastor") {
	capability "Actuator"
	capability "Bridge"
	capability "Health Check"


	attribute "serialNumber", "string"
	attribute "networkAddress", "string"
	attribute "status", "string"
	attribute "username", "string"
	attribute "host", "string"

		command "discoverItems"
        command "discoverBulbs"
        command "discoverGroups"
        command "discoverScenes"
		command "discoverRules"
        command "pollItems"
        command "pollBulbs"
        command "pollGroups"
        command "pollScenes"
        command "pollRules"

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
        	standardTile("bridge", "device.username", width: 6, height: 4) {
        		state "default", label:"Hue Bridge", inactivelabel:true, icon:"st.Lighting.light99-hue", backgroundColor: "#cccccc"
        }
		valueTile("idNumber", "device.idNumber", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'MAC Address:\n ${currentValue}'
		}
		valueTile("networkAddress", "device.networkAddress", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'IP Address:\n ${currentValue}'
		}
		valueTile("username", "device.username", decoration: "flat", height: 2, width: 6, inactiveLabel: false) {
			state "default", label:'Username:\n ${currentValue}'
		}

	main "bridge"
	details(["bridge", "idNumber", "networkAddress", "username"])
	}
}

void installed() {
	log.debug "Installed with settings: ${settings}"
	sendEvent(name: "DeviceWatch-Enroll", value: "{\"protocol\": \"LAN\", \"scheme\":\"untracked\", \"hubHardwareId\": \"${device.hub.hardwareID}\"}")
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}

def initialize() {
    def commandData = parent.getCommandData(device.deviceNetworkId)
    log.debug "Initialize Bridge ${commandData}"
    sendEvent(name: "idNumber", value: commandData.deviceId, displayed:true, isStateChange: true)
    sendEvent(name: "networkAddress", value: commandData.ip, displayed:false, isStateChange: true)
    sendEvent(name: "username", value: commandData.username, displayed:false, isStateChange: true)
    state.host = this.device.currentValue("networkAddress") + ":80"
    state.userName = this.device.currentValue("username")
    state.initialize = true
}


def discoverItems(inItems = null) {
	log.trace "discoverItems: ${discoverItems} (${inItems})"
	if (state.initialize != true ) { initialize() }
 	if (state.user == null ) { initialize() }

   		def host = state.host
		def username = state.userName

	  	log.debug "*********** ${host} ********"
		log.debug "*********** ${username} ********"
		def result


		log.trace "discoverItems: Bridge discovering all items on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/",
			headers: [
				HOST: host
			]
		)
/**
	if	(inItems == "Bulbs") {
		log.trace "Bridge discovering all BULBS on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/lights/",
			headers: [
				HOST: host
			]
		)
    } else if (inItems == "Groups") {
	log.trace "Bridge discovering all GROUPS on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/groups/",
			headers: [
				HOST: host
			]
		)
    } else if (inItems == "Scenes") {
	log.trace "Bridge discovering all SCENES on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/scenes/",
			headers: [
				HOST: host
			]
		)
    } else if (inItems == "Rules") {
	log.trace "Bridge discovering all RULES on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/rules/",
			headers: [
				HOST: host
			]
		)
	} else {
	log.trace "Bridge discovering ALL DEVICES on Hue hub."
	    result = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/api/${username}/",
			headers: [
				HOST: host
			]
		)
	}
**/


	log.debug("result: $result")
	return result
}

def pollItems() {
	log.trace "pollItems: polling state of all items from Hue hub."

	def host = state.host
	def username = state.userName

	sendHubCommand(new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/",
		headers: [
			HOST: host
		]
	))

}

def discoverBulbs() {
	log.trace "discoverBulbs: discovering bulbs from Hue hub."

	def host = state.host
	def username = state.userName

	def result = new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/lights/",
		headers: [
			HOST: host
		]
	)

    return result
}

def pollBulbs() {
	log.trace "ollBulbs: polling bulbs state from Hue hub."

	def host = state.host
	def username = state.userName

	sendHubCommand(new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/lights/",
		headers: [
			HOST: host
		]
	))

}

def discoverGroups() {
	log.debug("discoverGroups: discovering groups from Hue hub.")

	def host = state.host
	def username = state.userName

	def result = new physicalgraph.device.HubAction(
		method: "GET",
		path: "/api/${username}/groups/",
		headers: [
			HOST: host
		]
	)

	return result
}

def pollGroups() {
	log.trace "pollGroups: polling groups state from Hue hub."

	def host = state.host
	def username = state.userName

	sendHubCommand(new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/groups/",
		headers: [
			HOST: host
		]
	))

}

def pollScenes() {
	log.trace "pollGroups: polling scenes state from Hue hub."

	def host = state.host
	def username = state.userName

	sendHubCommand(new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/scenes/",
		headers: [
			HOST: host
		]
	))

}

def discoverRules() {
	log.trace "discoverRules: discovering rules from Hue hub."

	def host = state.host
	def username = state.userName

	def result = new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/rules/",
		headers: [
			HOST: host
		]
	)

    return result
}

def pollRules() {
	log.trace "pollRules: polling rules state from Hue hub."

	def host = state.host
	def username = state.userName

	sendHubCommand(new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/${username}/rules/",
		headers: [
			HOST: host
		]
	))

}

def handleParse(desc) {

	log.trace "handleParse()"
	parse(desc)

}


// parse events into attributes

def parse(String description) {

	log.trace "parse()"
	log.debug("description: $description")
	def parsedEvent = parseLanMessage(description)
	if (parsedEvent.headers && parsedEvent.body) {
		def headerString = parsedEvent.headers.toString()
		if (headerString.contains("application/json")) {
			def body = new groovy.json.JsonSlurper().parseText(parsedEvent.body)
			log.debug("body: $body")
			def bridge = parent.getBridge(parsedEvent.mac)
      def group

			/* responses from bulb/group/scene/rules command. Figure out which device it is, then pass it along to the device. */
// log.debug("body.class: ${body}")
log.debug("her6")
log.debug("bodynul: $body[0]") //+ .toString()
			if (body[0] != null && body[0].Success != null) {
				log.debug("her7")
				log.debug("body0: $body[0]") //+ .toString()
				body.each {
					log.debug("her3")
					it.success.each { k, v ->
						def spl = k.split("/")
						log.debug "k = ${k}, split1 = ${spl[1]}, split2 = ${spl[2]}, split3 = ${spl[3]}, value = ${v}"	//, split3 = ${spl[3]}, split4= ${spl[4]}
						def devId = ""
  	        def d

						// RULE STATUS RESPONSE
						if (spl[1] == "rules" ) {
							log.trace "HBS Bridge Response (RULE) == ${body}"
							devId = bridge.value.mac + "/RULE" + spl[2]
							log.debug "rule devID = ${devID} || value = ${v}"
							log.debug("her3")
							d = parent.getChildDevice(devId)
             	log.trace "Update rule ${spl[2]}' attribute ${spl[3]} with value ${v}"
              d.updateStatus("rule", spl[3], v)
						} else if (spl[1] == "scene" ) {
							// SCENES RESPONSES
							log.trace "HBS Bridge Response (SCENE) == ${body}"
							log.debug "Scene ${d.label} successfully run on group ${groupScene}."
							devId = bridge.value.mac + "/SCENE" + v
							d = parent.getChildDevice(devId)
							log.debug "k = ${k}, split1 = ${spl[1]}, split2 = ${spl[2]}, split3 = ${spl[3]}, value = ${v}"	//, split3 = ${spl[3]}, split4= ${spl[4]}
							log.trace "Update rule ${spl[2]}s attribute ${spl[3]} with value ${v}"
							d.updateStatus(spl[3], spl[4], v)
						} else if (spl[1] == "groups" ) {
							// GROUPS RESPONSES
							log.trace "HBS Bridge Response (GROUP) == ${body}"
							devId = bridge.value.mac + "/GROUP" + spl[2]

							//log.debug "GROUP: devId = ${devId}"
							// UPDATE THE GROUP (if not GROUP 0)
							if (spl[2] != "0") {

								d = parent.getChildDevice(devId)
								d.updateStatus(spl[3], spl[4], v)
								//NOW UPDATE THE LIGHTS IN THE GROUP
								def gLights = []

								gLights = parent.getGLightsDNI(spl[2], bridge.value.mac)
								gLights.each { gl ->
									if(gl != null){
										gl.updateStatus("state", spl[4], v)
										// 	          		log.debug "GLight ${gl}"
									}
								}
							} else {
									//NEED TO UPDATE ALL BULBS
							}
						} else if (spl[1] == "lights") {
							// LIGHTS RESPONSES
							log.trace "HBS Bridge Response (BULB) == ${body}"
							spl[1] = "BULBS"
							devId = bridge.value.mac + "/" + spl[1].toUpperCase()[0..-2] + spl[2]
							d = parent.getChildDevice(devId)
							d.updateStatus(spl[3], spl[4], v)
							} else {
								log.warn "Response contains unknown device type ${ spl[1] } ."
							}
					}
				} // body each
			} else if (!body) {
				log.warn "Error: ${body}"
			} else if (bridge) {
				log.trace "HBS Bridge: Doesn't appear to be a response (no success/error), so updating hue device list"
				def bulbs = [:]
				def groups = [:]
				def scenes = [:]
				def rules = [:]

				body?.lights?.each { k, v ->
					bulbs[k] = [id: k, label: v.name, type: v.type, state: v.state]
				}
				state.bulbs = bulbs

				body?.groups?.each { k, v ->
					groups[k] = [id: k, label: v.name, type: v.type, action: v.action, all_on: v.state.all_on, any_on: v.state.any_on, lights: v.lights] //, groupLightDevIds: devIdsGLights]
				}
				state.groups = groups

				body.scenes?.each { k, v ->
					//log.trace "k=${k} and v=${v}"
					scenes[k] = [id: k, label: v.name, type: "scene", lights: v.lights]
				}
				state.scenes = scenes

				body.rules?.each { k, v ->
					//	            	log.trace "k=${k} and v=${v}"
					//                  log.trace "************"

					rules[k] = [id: k, label: v.name, type: "rule", status: v.status, conditions: v.conditions, actions: v.actions]
				}
				state.rules = rules

				return createEvent(name: "itemDiscovery", value: device.hub.id, isStateChange: true, data: [bulbs, scenes, groups, rules, bridge.value.mac])
			}
		} else {
			log.debug("Unrecognized messsage: ${parsedEvent.body}")
		}
	}
	return []
}
